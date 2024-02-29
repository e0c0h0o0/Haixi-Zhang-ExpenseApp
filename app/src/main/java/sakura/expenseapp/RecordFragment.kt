package sakura.expenseapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Spinner
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.Date
import java.util.concurrent.Executors

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RecordFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RecordFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var addButton: FloatingActionButton
    lateinit var startDatePicker: Button
    lateinit var endDatePicker: Button
    lateinit var categoryPicker: Spinner
    lateinit var recordList: RecyclerView

    lateinit var startDate: Date
    lateinit var endDate: Date

    lateinit var recordAdapter: RecordListAdapter
    lateinit var database: AppDatabase


    lateinit var navController: NavController

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
            .setQueryCallback(RoomDatabase.QueryCallback
                { sqlQuery, bindArgs ->
                    Log.wtf("***", "SQL Query: $sqlQuery SQL Args: $bindArgs")
                },
                Executors.newSingleThreadExecutor()
            )
            .build()

    }

    fun triggerRefreshListView() {
        Log.wtf("***", "triggerRefreshListView called.")
        val current = Calendar.getInstance()
        if(!this::endDate.isInitialized) {
            endDate = current.time
        }
        current.add(Calendar.YEAR, -10)
        if(!this::startDate.isInitialized) {
            startDate = current.time
        }

        if(categoryPicker.selectedItemPosition - 1 >= 0) {
            recordAdapter.updateData(database.recordDao().filterByCategoryAndDate(
                categoryPicker.selectedItemPosition - 1,
                startDate.time,
                endDate.time
            ))
        }
        else{
            recordAdapter.updateData(database.recordDao().filterByDate(startDate.time, endDate.time))
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_record, container, false)

        startDatePicker = root.findViewById(R.id.u_start_date)
        endDatePicker = root.findViewById(R.id.u_end_date)
        categoryPicker = root.findViewById(R.id.u_category_picker)
        recordList = root.findViewById(R.id.u_record_list)

        recordAdapter = RecordListAdapter(arrayListOf(), requireContext())
        recordList.adapter = recordAdapter
        recordList.layoutManager = LinearLayoutManager(requireContext())

        categoryPicker.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                triggerRefreshListView()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) { }

        }

        startDatePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    startDate = Date(y - 1900, m, d)
                    startDatePicker.text = "${y}-${m}-${d}"
                    triggerRefreshListView()
                },
                Calendar.getInstance().get(Calendar.YEAR) - 10,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
            triggerRefreshListView()
        }
        endDatePicker.setOnClickListener {
            DatePickerDialog(
                requireContext(),
                { _, y, m, d ->
                    endDate = Date(y - 1900, m, d)
                    endDatePicker.text = "${y}-${m}-${d}"
                    triggerRefreshListView()
                },
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show()
        }



        addButton = root.findViewById(R.id.u_button_add)
        addButton.setOnClickListener {
            navController.navigate(R.id.action_recordFragment_to_editFragment)
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RecordFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RecordFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}