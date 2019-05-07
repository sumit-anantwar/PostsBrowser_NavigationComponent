package com.sumitanantwar.postsbrowser.mobile.ui.postdetails

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import butterknife.BindView
import com.bumptech.glide.Glide
import com.sumitanantwar.mvi.MviFragment
import com.sumitanantwar.postsbrowser.mobile.R
import com.sumitanantwar.postsbrowser.mobile.application.di.ViewModelFactory
import com.sumitanantwar.presentation.model.Post
import com.sumitanantwar.presentation.postdetails.PostDetailsIntent
import com.sumitanantwar.presentation.postdetails.PostDetailsViewModel
import com.sumitanantwar.presentation.postdetails.PostDetailsViewState
import com.sumitanantwar.presentation.model.User
import dagger.android.support.AndroidSupportInjection
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import javax.inject.Inject

class PostDetailsFragment :
    MviFragment<PostDetailsIntent, PostDetailsViewState>() {

    //======= ButterKnife Binders =======

    @BindView(R.id.image_profile)
    lateinit var imageViewProfile: ImageView

    @BindView(R.id.text_post_id)
    lateinit var textPostId: TextView

    @BindView(R.id.text_post_title)
    lateinit var textPostTitle: TextView

    @BindView(R.id.text_post_body)
    lateinit var textPostBody: TextView

    @BindView(R.id.text_username)
    lateinit var textUserName: TextView

    @BindView(R.id.text_comment_count)
    lateinit var textCommentCount: TextView

    @BindView(R.id.progress_comments)
    lateinit var progressBarComments: ProgressBar


    //======= Injections =======
    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    //======= ViewModel =======
    private val viewModel: PostDetailsViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(this, viewModelFactory)
            .get(PostDetailsViewModel::class.java)
    }


    //======= Publishers =======
    private val loadPostsCommentsPublisher = PublishSubject.create<PostDetailsIntent.LoadPostCommentsIntent>()


    //======= Private Properties =======
    lateinit var post: Post
    lateinit var user: User


    //======= Anstract =======
    override val layoutId: Int = R.layout.post_details_fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val args = PostDetailsFragmentArgs.fromBundle(arguments!!)
        post = args.post
        user = args.user
    }


    override fun onViewBound(view: View) {
//        setTitle("Post Details")
//        setDisplayHomeAsUpEnabled(true)
        AndroidSupportInjection.inject(this)

        textPostTitle.text = post.title
        textPostBody.text = post.body
        textPostId.text = post.id.toString()
        textUserName.text = user.username

        Glide.with(activity!!)
            .load(user.profileImageUrl)
            .into(imageViewProfile)

        bindIntents()

        loadPostsCommentsPublisher.onNext(PostDetailsIntent.LoadPostCommentsIntent(post.id))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
//            router.popCurrentController()
            return true
        }

        return false
    }


    //======= MVI =======
    override fun bindIntents() {
        viewModel.states().subscribeBy(
            onNext = {
                this.render(it)
            },
            onError = {
                Timber.e(it)
            }
        ).addTo(disposables)

        viewModel.processIntents(intents())
    }

    override fun intents(): Observable<PostDetailsIntent> {
        return loadPostCommentsIntent().cast(PostDetailsIntent::class.java)
    }

    override fun render(state: PostDetailsViewState) {

        if (state.isLoading) {
            progressBarComments.visibility = View.VISIBLE
            textCommentCount.visibility = View.INVISIBLE
        }
        else {
            progressBarComments.visibility = View.GONE
            textCommentCount.visibility = View.VISIBLE
        }

        if (!state.comments.isEmpty()) {
            textCommentCount.text = String.format(getString(R.string.label_comments_count), state.comments.count())
        }
        else {
            textCommentCount.text = getString(R.string.label_no_comments)
        }

        if (state.error != null) {
            textCommentCount.text = getString(R.string.error_loading_comments)
        }
    }

    //======= Observables from Publishers =======
    private fun loadPostCommentsIntent(): Observable<PostDetailsIntent.LoadPostCommentsIntent> {
        return loadPostsCommentsPublisher
    }
}
