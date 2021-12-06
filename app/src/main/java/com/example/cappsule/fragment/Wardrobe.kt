package com.example.cappsule.fragment

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cappsule.R
import com.example.cappsule.adapter.ArticleRecyclerAdapter
import com.example.cappsule.adapter.ArticleRecyclerAdapter.OnClickArticleListener
import com.example.cappsule.database.DatabaseHelperArticle
import com.example.cappsule.dialog.ArticleEditDialog
import com.example.cappsule.dialog.DeleteDialog
import com.example.cappsule.getBytes
import com.example.cappsule.getImage
import com.example.cappsule.objects.Article
import com.example.cappsule.toaster
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*


class Wardrobe : Fragment(), OnClickArticleListener {
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var databaseHelperArticle: DatabaseHelperArticle
    private lateinit var recyclerview: RecyclerView
    private lateinit var articleRecyclerAdapter: ArticleRecyclerAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private val articles = ArrayList<Article>()
    private lateinit var imageViewEmpty: ImageView
    private lateinit var textViewEmpty: TextView
    private lateinit var textViewEmpty2: TextView
    var stringType: String? = null
    var stringWarmth: String? = null
    var stringAvailability: String? = null
    var integerAvailability: Int? = null
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private lateinit var progressBar: ProgressBar
    private lateinit var frameWardrobe: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelperArticle = DatabaseHelperArticle(context)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                newPicture(data)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_wardrobe, container, false)
        frameWardrobe = view.findViewById(R.id.frame_wardrobe)
        progressBar = requireActivity().requireViewById(R.id.progressBar2)
        fabAdd = view.findViewById(R.id.add_article_button)
        fabAdd.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch(intent)
        }
        val spinnerType = view.findViewById<Spinner>(R.id.article_list_type)
        val spinnerWarmth = view.findViewById<Spinner>(R.id.article_list_warmth)
        val spinnerAvailability = view.findViewById<Spinner>(R.id.article_list_availability)
        imageViewEmpty = view.findViewById(R.id.imageViewEmpty)
        textViewEmpty = view.findViewById(R.id.textViewEmpty)
        textViewEmpty2 = view.findViewById(R.id.textViewEmpty3)
        recyclerview = view.findViewById(R.id.article_list)
        layoutManager = LinearLayoutManager(context)
        recyclerview.layoutManager = layoutManager
        articleRecyclerAdapter = ArticleRecyclerAdapter(activity!!, articles, databaseHelperArticle, this)
        recyclerview.adapter = articleRecyclerAdapter
        setItemSelected(spinnerType, "Any type")
        setItemSelected(spinnerWarmth, "Any warmth")
        setItemSelected(spinnerAvailability, "Any availability")

        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.swiperefresh)
        pullToRefresh.setOnRefreshListener {
            populateDataSet() // your code
            pullToRefresh.isRefreshing = false
        }

        return view
    }

    @Suppress("EXPERIMENTAL_IS_NOT_ENABLED")
    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.Main) {
            frameWardrobe.visibility = View.INVISIBLE
            progressBar.visibility = View.VISIBLE
            populateDataSet()
            delay(500)
            progressBar.visibility = View.GONE
            frameWardrobe.visibility = View.VISIBLE
        }
    }

    fun populateDataSet() {
        articles.clear()
        val cursor = databaseHelperArticle.data
        while (cursor.moveToNext()) {
            val article = Article(
                getImage(cursor.getBlob(0)),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getInt(1),
                cursor.getInt(4)
            )
            if ((stringType == resources.getString(R.string.article_type) || stringType == article.type)
                && (stringWarmth == resources.getString(R.string.article_warmth) || stringWarmth == article.warmth)
                && (stringAvailability == resources.getString(R.string.Availability) || integerAvailability == article.availability)
            ) {
                articles.add(article)
            }
            articleRecyclerAdapter.notifyDataSetChanged()
        }
        if (articleRecyclerAdapter.itemCount == 0) {
            imageViewEmpty.visibility = View.VISIBLE
            textViewEmpty.visibility = View.VISIBLE
            textViewEmpty2.visibility = View.VISIBLE
            if(stringType != resources.getString(R.string.article_type) || stringWarmth != resources.getString(R.string.article_warmth) || stringAvailability != resources.getString(R.string.Availability)){
                textViewEmpty2.text = getString(R.string.tryDiff)
            } else {
                textViewEmpty2.text = getString(R.string.EmptyWardrobe)
            }
        } else {
            imageViewEmpty.visibility = View.GONE
            textViewEmpty.visibility = View.GONE
            textViewEmpty2.visibility = View.GONE
        }
    }

    private fun setItemSelected(spinner: Spinner, string: String?) {
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View, position: Int, id: Long) {
                when (string) {
                    "Any type" -> stringType = spinner.selectedItem.toString()
                    "Any warmth" -> stringWarmth = spinner.selectedItem.toString()
                    "Any availability" -> {
                        stringAvailability = spinner.selectedItem.toString()
                        if (stringAvailability == getString(R.string.Available)) {
                            integerAvailability = 1
                        } else if (stringAvailability == getString(R.string.Unavailable)) {
                            integerAvailability = 0
                        }
                    }
                }
                populateDataSet()
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun newPicture(data: Intent?) {
            val extras = data!!.extras
            val imageBitmap = extras!!["data"] as Bitmap?
            databaseHelperArticle.addArticle(getBytes(imageBitmap), 1, requireContext())
            populateDataSet()
    }

    override fun onClickArticleListener(position: Int) {
        val rightpos = articles[position].id
        val cursor = databaseHelperArticle.data
        var type: String? = ""
        var warmth: String? = ""
        var image: Bitmap? = null
        while (cursor.moveToNext()) {
            if (cursor.getInt(4) == rightpos) {
                image = getImage(cursor.getBlob(0))
                type = cursor.getString(2)
                warmth = cursor.getString(3)
            }
        }
        val articleEditDialog = ArticleEditDialog()
        articleEditDialog.bitmap = image
        articleEditDialog.type = type
        articleEditDialog.warmth = warmth
        articleEditDialog.position = rightpos
        articleEditDialog.wardrobe = this
        articleEditDialog.show(childFragmentManager, "ARTICLEEDITDIALOG")
    }

    override fun onClickDeleteListener(position: Int) {
        val deleteDialog = DeleteDialog()
        deleteDialog.position = position
        deleteDialog.wardrobe = this
        deleteDialog.show(childFragmentManager, deleteDialog.tag)
    }

    fun actualDeletion(position: Int) {
        val rightpos = articles[position].id
        databaseHelperArticle.deleteArticle(rightpos)
        articles.removeAt(position)
        articleRecyclerAdapter.notifyDataSetChanged()
        populateDataSet()
        toaster(context, resources.getString(R.string.ArticleDeleted))
    }
}