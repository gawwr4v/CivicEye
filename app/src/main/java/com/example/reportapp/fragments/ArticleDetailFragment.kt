package com.example.reportapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.reportapp.R

class ArticleDetailFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "article_title"

        fun newInstance(title: String): ArticleDetailFragment {
            val fragment = ArticleDetailFragment()
            val args = Bundle()
            args.putString(ARG_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_article_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE) ?: "Article"

        val topBarTitle: TextView = view.findViewById(R.id.topBarTitle)
        val titleView: TextView = view.findViewById(R.id.articleTitle)
        val contentView: TextView = view.findViewById(R.id.articleContent)
        val btnBack: ImageView = view.findViewById(R.id.btnBack)

        // ✅ Update top bar + article title
        topBarTitle.text = title
        titleView.text = title

        // ✅ Load article content
        contentView.text = when (title) {
            "How to use Civic Eye" -> getCivicEyeArticle()
            "5 ways to be a responsible citizen" -> "👉 Participate in community cleanups...\n👉 Follow traffic rules...\n👉 Help others report issues...\n👉 Stay informed...\n👉 Promote awareness."
            "Report issues effectively" -> "📌 Always provide clear photos and accurate details when reporting..."
            "Community guidelines" -> "✅ Respect others. ✅ Report responsibly. ✅ Avoid misuse of the app."
            else -> "Article not found."
        }

        // ✅ Back button action
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun getCivicEyeArticle(): String {
        return """
            📖 How to use Civic Eye

            Civic Eye helps citizens report local issues like potholes, broken streetlights, garbage dumps, and other civic problems directly to the concerned authorities. Here’s how it works:

            1️⃣ Open the app and go to the Quick Access → File a Report section.  
            2️⃣ Select the type and enter the description of the issue clearly.  
            3️⃣ Take or upload a photo to support your report.  
            4️⃣ Allow location access so the issue can be pinned on the map.  
            5️⃣ Submit the report ✅.  

            After submission:  
            - Your report will be marked as "Under Verification".  
            - Authorities or community managers will change it to "Ongoing" when work starts.  
            - Once fixed, it will be marked "Resolved".  

            You can always track your reports under Quick Access → Track Reports.  

            Civic Eye makes reporting transparent and easy — empowering every citizen to make their city better! 🌍
        """.trimIndent()
    }
}
