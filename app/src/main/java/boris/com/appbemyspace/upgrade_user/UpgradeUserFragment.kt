package boris.com.appbemyspace.upgrade_user


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import boris.com.appbemyspace.R
import boris.com.appbemyspace.data.model.EmptyResultDataModel
import boris.com.appbemyspace.data.prefs.AppReferencesHelper
import boris.com.appbemyspace.data.prefs.AppReferencesHelperImpl
import boris.com.appbemyspace.databinding.FragmentUpgradeUserBinding
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*


class UpgradeUserFragment : Fragment() {

    private lateinit var viewmodel: UpgradeUserViewModel
    private lateinit var referencesHelper: AppReferencesHelper
    private lateinit var binding: FragmentUpgradeUserBinding

    private lateinit var calendar: Calendar

    internal lateinit var adapter: AutoCompleteAdapter

    internal lateinit var placesClient: PlacesClient
    internal lateinit var autoCompleteTextView: AutoCompleteTextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_upgrade_user,
            container,
            false
        )

        referencesHelper = AppReferencesHelperImpl(this.context!!)

        val viewModelFactory =
            UpgareUserViewModelFactory(referencesHelper.getCurrentUserToken()!!, referencesHelper.getUsername()!!)
        viewmodel = ViewModelProviders.of(this, viewModelFactory).get(UpgradeUserViewModel::class.java)

        binding.upgradeViewmodel = viewmodel
        binding.lifecycleOwner = this

        onDateChangeListener()

        binding.cancelBtn.setOnClickListener(
            Navigation.createNavigateOnClickListener(R.id.action_upgradeUserFragment_to_userProfileFragment)
        )


        if (!Places.isInitialized()) {
            Places.initialize(this.context!!.applicationContext, getString(R.string.google_api_key))
        }

        // autoCompleteTextView= binding.findViewById(R.id.city_country_inputtext)
        placesClient = Places.createClient(this.requireContext())
        binding.cityCountryInputtext.threshold = 1
        // autoCompleteTextView.onItemClickListener = autocompleteClickListener
        adapter = AutoCompleteAdapter(this.requireContext(), placesClient)
        binding.cityCountryInputtext.setAdapter(adapter)



        viewmodel.isUpgradeUser.observe(this, Observer<EmptyResultDataModel> { result ->

            if (result.code == 200) {
                referencesHelper.saveUserState(true)
                Navigation.createNavigateOnClickListener(R.id.action_upgradeUserFragment_to_userProfileFragment)
            } else {
                println("******************   HATA  *****************")
            }

        })


        return binding.root
    }


    fun onDateChangeListener() {
        calendar = Calendar.getInstance()
        val thisYear = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        binding.datePicker.init(
            thisYear,
            month,
            day,
            DatePicker.OnDateChangedListener { view, year, monthOfYear, dayOfMonth ->

                viewmodel.getBirthDate.value =
                    dayOfMonth.toString() + "/" + monthOfYear.toString() + "/" + year.toString()
            }
        )
    }





}
