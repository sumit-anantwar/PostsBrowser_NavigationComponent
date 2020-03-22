package com.sumitanantwar.postsbrowser.mobile.ui.postlist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

import butterknife.BindView
import butterknife.OnClick
import com.jakewharton.rxbinding3.widget.textChanges

import com.sumitanantwar.mvi.MviFragment
import com.sumitanantwar.postsbrowser.mobile.R
import com.sumitanantwar.postsbrowser.mobile.application.di.ViewModelFactory
import com.sumitanantwar.postsbrowser.mobile.util.HeightProperty
import com.sumitanantwar.presentation.model.Post
import com.sumitanantwar.presentation.model.User
import com.sumitanantwar.presentation.postslist.PostsListIntent
import com.sumitanantwar.presentation.postslist.PostsListViewModel
import com.sumitanantwar.presentation.postslist.PostsListViewState
import com.sumitanantwar.repository.scheduler.SchedulerProvider
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class PostsListFragment :
    MviFragment<PostsListIntent, PostsListViewState>(),
    PostListAdapterCallBackListener {

    //======= ButterKnife Bindings =======
    @BindView(R.id.filter_container)
    lateinit var filterContiner: ViewGroup

    @BindView(R.id.filter_bar)
    lateinit var filterBar: ViewGroup

    @BindView(R.id.layout_filter)
    lateinit var filterPanel: ViewGroup

    @BindView(R.id.edit_text_userid)
    lateinit var editTextUserId: EditText

    @BindView(R.id.edit_text_title)
    lateinit var editTextTitle: EditText

    @BindView(R.id.edit_text_body)
    lateinit var editTextBody: EditText

    @BindView(R.id.recycler_posts)
    lateinit var postsRecyclerView: RecyclerView

    @BindView(R.id.swiperefresh_posts)
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    @BindView(R.id.text_error)
    lateinit var errorTextView: TextView


    //======= Injections =======
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var postsListAdapter: PostListAdapter


    //======= ViewModel =======
    private val viewModel: PostsListViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, viewModelFactory)
            .get(PostsListViewModel::class.java)
    }

    //======= Publishers =======
    private val loadPostsWithFilterPublisher =
        PublishSubject.create<PostsListIntent.LoadPostsWithFilterIntent>()
    private val loadAllUsersPublisher = PublishSubject.create<PostsListIntent.LoadAllUsersIntent>()


    //======= Abstract =======
    override val layoutId: Int = R.layout.posts_list_fragment


    override fun onViewBound(view: View) {

        AndroidSupportInjection.inject(this)

        // Set Controller Title
//        setTitle(applicationContext?.getString(R.string.title_posts_list))
//        setDisplayHomeAsUpEnabled(false)

        // Setup RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(context)
        postsRecyclerView.adapter = postsListAdapter
        postsListAdapter.setCallBackListener(this)

        // Swipe Refresh Listener
        swipeRefreshLayout.setOnRefreshListener {
            fetchPosts()
        }

        val dividerDecoration =
            DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        dividerDecoration.setDrawable(
            ContextCompat.getDrawable(
                context!!,
                R.drawable.divider_shape
            )!!
        )
        postsRecyclerView.addItemDecoration(dividerDecoration)

        rxSetup()
    }


    //======= Button Click Listeners =======
    @OnClick(R.id.button_filter)
    fun onClickFilterButton() {
        toggleFilterPanelVisibility()
    }


    //======= Private Methods =======
    private fun rxSetup() {

        val userIdObservable = editTextUserId.textChanges()
            .map { it.toString() }
            .distinctUntilChanged()

        val titleObservable = editTextTitle.textChanges()
            .map { it.toString() }
            .distinctUntilChanged()

        val bodyObservable = editTextBody.textChanges()
            .map { it.toString() }
            .distinctUntilChanged()

        Observables.combineLatest(userIdObservable, titleObservable, bodyObservable)
            .debounce(300, TimeUnit.MILLISECONDS)
            .observeOn(schedulerProvider.ui())
            .subscribe {
                val userId = it.first
                val title = it.second
                val body = it.third

                fetchPostsWithFilter(userId, title, body)

            }.addTo(disposables)

    }

    private fun fetchPosts() {

        val userId = editTextUserId.text.toString()
        val title = editTextTitle.text.toString()
        val body = editTextBody.text.toString()

        fetchPostsWithFilter(userId, title, body)
    }

    private fun fetchPostsWithFilter(userId: String, title: String, body: String) {

        loadPostsWithFilterPublisher.onNext(
            PostsListIntent.LoadPostsWithFilterIntent(
                userId,
                title,
                body
            )
        )
    }

    /** Toggles the Visibility of the Filter Panel */
    private fun toggleFilterPanelVisibility() {

        // Check the currnt state of the Filter Panel
        val isFilterPanelHidden = (filterPanel.visibility == View.INVISIBLE)

        // Make the panel visible, so that the animation can be seen
        filterPanel.visibility = View.VISIBLE

        // Store the current dimensions of the views to be animated
        val filterContainerHeight = filterContiner.height.toFloat()
        val filterBarHeight = filterBar.height.toFloat()
        val filterPanelHeight = filterPanel.height.toFloat()

        // Calculate the new positions
        var filterPanelY = filterBarHeight
        var newFilterContainerHeight = filterBarHeight + filterPanelHeight
        if (!isFilterPanelHidden) {
            filterPanelY = -filterPanelHeight
            newFilterContainerHeight = filterBarHeight
        }

        // Set the view layer types to Hardware for smoother animation
        filterPanel.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        filterContiner.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        // Create an animator set and play the animations together
        AnimatorSet().apply {
            duration = 300

            playTogether(
                ObjectAnimator.ofFloat(filterPanel, "y", filterPanelY),
                ObjectAnimator.ofFloat(
                    filterContiner,
                    HeightProperty(),
                    filterContainerHeight,
                    newFilterContainerHeight
                )
            )

            addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        filterPanel.setLayerType(View.LAYER_TYPE_NONE, null)
                        filterContiner.setLayerType(View.LAYER_TYPE_NONE, null)

                        filterPanel.visibility =
                            if (isFilterPanelHidden) View.VISIBLE else View.INVISIBLE
                    }
                }
            )
        }.start()
    }

    //======= PostListAdapterCallbackListener =======
    override fun onClickPost(post: Post, user: User) {

        findNavController().navigate(
            PostsListFragmentDirections.actionNext(post, user)
        )
    }

    //======= MVI =======
    override fun bindIntents() {
        viewModel.states().subscribe {
            this.render(it)
        }.addTo(disposables)

        viewModel.processIntents(intents())

        // Load all the Users at launch
        loadAllUsersPublisher.onNext(PostsListIntent.LoadAllUsersIntent)
    }

    override fun intents(): Observable<PostsListIntent> {

        return Observable.merge(
            loadPostsWithFilterIntent(),
            loadAllUsersIntent()
        )
    }

    override fun render(state: PostsListViewState) {

        swipeRefreshLayout.isRefreshing = state.isLoading

        if (state.isLoading) {
            postsRecyclerView.visibility = if (state.posts.isEmpty()) View.GONE else View.VISIBLE
            errorTextView.visibility = View.GONE

            return
        }

        val error = state.error
        if (error != null) {

            Toast.makeText(activity, error.localizedMessage, Toast.LENGTH_SHORT).show()

            postsRecyclerView.visibility = View.GONE
            errorTextView.visibility = View.VISIBLE

            errorTextView.text = activity?.getString(R.string.error_fetching_posts)

            return
        }

        if (!state.posts.isEmpty()) {
            postsRecyclerView.visibility = View.VISIBLE
            errorTextView.visibility = View.GONE

            postsListAdapter.updatePosts(state.posts, state.users)
        }
        else {
            errorTextView.visibility = View.VISIBLE
            postsRecyclerView.visibility = View.GONE

            errorTextView.text = activity?.getString(R.string.error_empty_posts)
        }
    }

    //======= Observables from Publishers =======
    private fun loadPostsWithFilterIntent(): Observable<PostsListIntent.LoadPostsWithFilterIntent> {
        return loadPostsWithFilterPublisher
    }

    private fun loadAllUsersIntent(): Observable<PostsListIntent.LoadAllUsersIntent> {
        return loadAllUsersPublisher
    }
}
