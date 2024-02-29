package sakura.expenseapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.addCallback
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [EditFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class EditFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var navController: NavController
    var year: Int = 0
    var month: Int = 0
    var day: Int = 0
    lateinit var datePickerButton: Button
    lateinit var categoryComboBox: Spinner
    lateinit var submitButton: Button
    lateinit var amountEdit: EditText
    lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        navController = findNavController()
        database = Room.databaseBuilder(requireContext(), AppDatabase::class.java, requireContext().packageName)
            .allowMainThreadQueries()
            .addMigrations()
            .build()



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_edit, container, false)
        datePickerButton = root.findViewById<Button>(R.id.u_date_picker)
        categoryComboBox = root.findViewById<Spinner>(R.id.u_category)
        submitButton = root.findViewById<Button>(R.id.u_submit)
        amountEdit = root.findViewById<EditText>(R.id.u_amount)

        submitButton.setOnClickListener {
            if(year == 0 || month == 0 || day == 0) {
                AlertDialog.Builder(context).setMessage("Please set date").show()
                return@setOnClickListener
            }

            if(amountEdit.text.trim().length == 0) {
                AlertDialog.Builder(context).setMessage("Please enter amount").show()
                return@setOnClickListener
            }

            val timestamp = Date(year - 1900, month, day).time



            val record = Record(null, timestamp, amountEdit.text.toString().toFloat(), categoryComboBox.selectedItemPosition)
            database.recordDao().insertRecord(record)

            Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show()

            navController.navigate(R.id.action_editFragment_to_recordFragment)

        }

        datePickerButton.setOnClickListener {
            context?.let { it1 ->
                DatePickerDialog(
                    it1,
                    DatePickerDialog.OnDateSetListener { datePicker, y, m, d ->
                        year = y
                        month = m
                        day = d

                        datePickerButton.text = "${y}-${m}-${d}"

                    },
                    Calendar.getInstance().get(Calendar.YEAR),
                    Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
            }
        }


        return root
    }

    override fun onResume() {
        super.onResume()
        // requireActivity().onBackPressedDispatcher.addCallback { }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}