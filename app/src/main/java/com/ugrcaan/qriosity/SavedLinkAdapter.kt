import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.ugrcaan.qriosity.R
import com.ugrcaan.qriosity.model.SavedLink

class SavedLinkAdapter(
    private val savedLinks: List<SavedLink>,
    private val webView: WebView,
    private val fab: FloatingActionButton
) : RecyclerView.Adapter<SavedLinkAdapter.SavedLinkViewHolder>() {

    class SavedLinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewName: TextView = itemView.findViewById(R.id.textViewName)
        val textViewLink: TextView = itemView.findViewById(R.id.textViewLink)
        val openLinkButton: ImageButton = itemView.findViewById(R.id.open_link_in_webview_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedLinkViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_saved_link_item, parent, false)
        return SavedLinkViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SavedLinkViewHolder, position: Int) {
        val savedLink = savedLinks[position]
        holder.textViewName.text = savedLink.name
        holder.textViewLink.text = savedLink.link

        holder.openLinkButton.setOnClickListener {
            loadUrlInWebView(savedLink.link)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun loadUrlInWebView(url: String) {
        fab.visibility = View.GONE //TODO This method will be removed when draft is finalized.
        webView.visibility = View.VISIBLE
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(url)
    }

    override fun getItemCount(): Int {
        return savedLinks.size
    }
}
