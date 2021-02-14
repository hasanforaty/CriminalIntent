package com.hasan.foraty.criminalintent

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.webkit.PermissionRequest
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hasan.foraty.criminalintent.dialog.PermissionFragment
import com.hasan.foraty.criminalintent.model.Crime
import com.hasan.foraty.criminalintent.model.CrimeDetailViewModel
import com.hasan.foraty.criminalintent.model.PermissionMassage
import com.hasan.foraty.criminalintent.model.TimePickerFragment
import java.io.File
import java.net.URI
import java.security.Permission
import java.security.PermissionCollection
import java.text.DateFormat
import java.util.*
import java.util.jar.Manifest

private const val ART_CRIME_ID="crime_id"
private const val DIALOG_DATE="DialogDate"
private const val DIALOG_TIME="DialogTime"
private const val REQUEST_DATE=0
private const val REQUEST_TIME=0
private const val REQUEST_CONTACT=1
private const val REQUEST_PHOTO=3
private const val DATE_FORMAT="EEE,MMM,dd,yyy"
private const val REQUEST_PERMISSION_CONTACT="0"
private const val REQUEST_CONTACT_DETAIL=2
class CrimeFragment private constructor() : Fragment(),DatePickerFragment.Callbacks,TimePickerFragment.Callback,PermissionFragment.callback {

    private lateinit var crime:Crime
    private lateinit var titleField:EditText
    private lateinit var dateButton:Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var timePickerButton: Button
    private lateinit var chooseSuspectButton: Button
    private lateinit var sendReportButton: Button
    private lateinit var callSuspectButton: Button
    private lateinit var crimeCamera:ImageButton
    private lateinit var crimePhoto:ImageView
    private lateinit var permissionMassageContact:PermissionMassage
    private lateinit var photoFile:File
    private lateinit var photoUri:Uri
    private val crimeDetailViewModel:CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    companion object{
        fun newInstance(id:UUID):CrimeFragment{
            val args=Bundle().apply {
                putSerializable(ART_CRIME_ID,id)
            }
            return CrimeFragment().apply {
                arguments=args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime=Crime()
        val crimeId:UUID=arguments?.getSerializable(ART_CRIME_ID) as UUID
        crimeDetailViewModel.loadCrime(crimeId)
        permissionMassageContact=PermissionMassage(getString(R.string.contact_permission_title),getString(R.string.contact_permission_message))
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_crime,container,false)

        titleField = view.findViewById(R.id.crime_title)
        dateButton = view.findViewById(R.id.crime_date)
        solvedCheckBox=view.findViewById(R.id.crime_solved)
        timePickerButton=view.findViewById(R.id.crime_time)
        chooseSuspectButton=view.findViewById(R.id.choose_suspect)
        sendReportButton=view.findViewById(R.id.send_report)
        callSuspectButton=view.findViewById(R.id.call_suspect)
        crimeCamera=view.findViewById(R.id.crime_camera)
        crimePhoto=view.findViewById(R.id.crime_photo)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(viewLifecycleOwner,{crime ->
            crime?.let {
                this.crime=crime
                photoFile=crimeDetailViewModel.getPhotoFile(crime)
                photoUri=FileProvider.getUriForFile(requireContext()
                        ,"come.hasan.foraty.criminalIntent.provider",
                        photoFile)
                crimePhoto.viewTreeObserver.addOnGlobalLayoutListener {
                    updatePhotoView()
                }
                updateUI()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher=object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title=s.toString()
            }
            override fun afterTextChanged(s: Editable?) {

            }
        }
        titleField.addTextChangedListener(titleWatcher)

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
        timePickerButton.setOnClickListener {
            TimePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_TIME)
            }
        }
        solvedCheckBox.apply {
            setOnCheckedChangeListener { buttonView, isChecked ->
                crime.isSolved = isChecked
            }
        }

        sendReportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type="text/plain"
                putExtra(Intent.EXTRA_TEXT,getCrimeReport())
                putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.crime_report_subject))
            }.also {intent ->
                val chooserIntent=Intent.createChooser(intent,getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        callSuspectButton.setOnClickListener {
            callingSuspect()
        }

        chooseSuspectButton.setOnClickListener {
            val pickContactIntent=Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI).also {
                startActivityForResult(it, REQUEST_CONTACT)
            }.apply {
                val packageManager=requireActivity().packageManager
                val resolveActivity:ResolveInfo?=packageManager.resolveActivity(this,PackageManager.MATCH_DEFAULT_ONLY)
                if (resolveActivity==null){
                    it.isEnabled=false
                }
            }
        }

        crimeCamera.apply {
            val packageManager =requireActivity().packageManager
            val captureImage=Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity:ResolveInfo?=packageManager
                    .resolveActivity(captureImage,PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity==null){
                isEnabled=false
            }
            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT,photoUri)
                val cameraActivates : List<ResolveInfo> = packageManager
                        .queryIntentActivities(captureImage,PackageManager.MATCH_DEFAULT_ONLY)
                for (cameraActivity in cameraActivates){
                    requireActivity().grantUriPermission(
                            cameraActivity.activityInfo.packageName,
                            photoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
                startActivityForResult(captureImage, REQUEST_PHOTO)
            }
        }
    }

    private fun updatePhotoView(){
        if (photoFile.exists()){
            val bitmap= getScaledBitmap(photoFile.path,crimePhoto.measuredWidth,crimePhoto.measuredHeight)
            crimePhoto.setImageBitmap(bitmap)
        }else{
         crimePhoto.setImageDrawable(null)
        }
    }

    private fun callingSuspect(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (requireContext().checkSelfPermission
                    (android.Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(),getText
                (R.string.contact_permission_granted),Toast.LENGTH_SHORT).show()
                requestACall()
            }else{
                askForContactPermission()
            }
        }else{
            requestACall()
        }
    }


    private fun askForContactPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (requireActivity(),android.Manifest.permission.READ_CONTACTS)){
                PermissionFragment.newInstance(permissionMassageContact).apply {
                    setTargetFragment(this@CrimeFragment, REQUEST_CONTACT_DETAIL)
                    show(this@CrimeFragment.requireFragmentManager(), REQUEST_PERMISSION_CONTACT)
                }
        }else{
            showContactPermission()
        }
    }

    private fun requestACall(){
        val displayName=chooseSuspectButton.text.toString()
        val contactURI=Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,Uri.encode(displayName))
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursor=requireContext().contentResolver.query(contactURI,projection,null,null,null)
        cursor?.use {
            if (it.count==0){
                return
            }
            it.moveToFirst()
            val number=it.getInt(0)
            val phoneNumber=Uri.parse("tel:$number")
            val intent=Intent(Intent.ACTION_DIAL,phoneNumber)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode== REQUEST_CONTACT_DETAIL){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                requestACall()
            }
        }
    }

    private fun showContactPermission(){
        val permissions= arrayOf(android.Manifest.permission.READ_CONTACTS)
        ActivityCompat.requestPermissions(requireActivity(),permissions, REQUEST_CONTACT_DETAIL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode!= Activity.RESULT_OK -> return
            requestCode== REQUEST_CONTACT && data!=null ->{
                val contactURI: Uri? =data.data
                val queryField= arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor= contactURI?.let {
                    requireActivity().contentResolver
                            .query(it,queryField,null,null,null)
                }

                cursor?.use {
                    if (it.count==0){
                       return
                    }
                    it.moveToFirst()
                    val suspect=it.getString(0)
                    crime.suspect=suspect
                    crimeDetailViewModel.saveCrime(crime)
                    chooseSuspectButton.text=suspect
                }
            }
            requestCode== REQUEST_PHOTO ->{
                updatePhotoView()
                revokeCameraPermission()
            }
        }
    }


    override fun onStop() {
        super.onStop()
        if (crime.title.isNotBlank()){
            crimeDetailViewModel.saveCrime(crime)
        }else{
            crimeDetailViewModel.deleteCrime(crime)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_crime,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.delete_crime->{
                crimeDetailViewModel.deleteCrime(crime)
                requireFragmentManager().popBackStack()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(){
        titleField.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.apply {
            isChecked=crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crime.suspect.isNotBlank()){
            chooseSuspectButton.text=crime.suspect
            callSuspectButton.visibility=View.VISIBLE
        }
    }
    override fun onDateSelected(date: Date) {
        crime.date=date
        updateUI()
    }

    override fun onTimeSelected(date: Date) {
        crime.date=date
        updateUI()
    }

    private fun getCrimeReport():String{
        val solvedString=if (crime.isSolved){
            getString(R.string.crime_report_solved)
        }else{
            getString(R.string.crime_report_unsolved)
        }
        val dateString=android.text.format.DateFormat.format(DATE_FORMAT,crime.date)
        val suspectString=if (crime.suspect.isBlank()){
            getString(R.string.crime_report_no_suspect)
        }else{
            getString(R.string.crime_report_suspect,crime.suspect)
        }
        return getString(R.string.crime_report,
                        crime.title,
                        dateString,
                        solvedString,
                        suspectString)
    }

    override fun onResponce() {
        showContactPermission()
    }

    override fun onDetach() {
        revokeCameraPermission()
        super.onDetach()
    }

    private fun revokeCameraPermission(){
        requireActivity()
                .revokeUriPermission(photoUri,Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }
}