import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.hunbbing.R
import com.example.hunbbing.SellListViewModel

class SearchFragment : Fragment() {
    private val viewModel by activityViewModels<SellListViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        val searchET = view.findViewById<EditText>(R.id.search_et)

        searchET.doAfterTextChanged {
            viewModel.searchProduct(it.toString())
        }

        return view
    }
}
