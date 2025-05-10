package edu.ifsp.com.br.redesocialrc.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import edu.ifsp.com.br.redesocialrc.FeedActivity
import edu.ifsp.com.br.redesocialrc.databinding.ItemPostBinding
import edu.ifsp.com.br.redesocialrc.model.Post

class PostAdapter(private val posts: Array<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]

        holder.binding.apply {
            userName.text = post.userName
            descricaoImage.text = post.title
            imagePost.setImageBitmap(post.image)
            profilePhotoUser.setImageBitmap(post.userProfilePhoto)

            root.setOnClickListener {
                val intent = Intent(root.context, FeedActivity::class.java)
                intent.putExtra("POST_ID", post.id)
                root.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}