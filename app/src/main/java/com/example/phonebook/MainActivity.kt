package com.example.phonebook

import android.Manifest
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phonebook.databinding.ActivityMainBinding

const val REQUEST_CODE = 1
const val ORDER = "ASC"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRecyclerView()

        binding.getContactsButton.setOnClickListener {
            it.isClickable = false
            checkPermission()
        }
    }

    private fun initRecyclerView() {
        binding.contactsRecyclerView.adapter = ContactsAdapter()

        binding.contactsRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
    }

    private fun checkPermission() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                    PackageManager.PERMISSION_GRANTED -> {
                getContacts()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.access))
                    .setMessage(getString(R.string.explanation))
                    .setPositiveButton(getString(R.string.accept)) { _, _ -> requestPermission() }
                    .setNegativeButton(getString(R.string.decline)) { dialog, _ ->
                        binding.getContactsButton.isClickable = true
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            else -> {
                requestPermission()
            }
        }
    }

    private fun requestPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getContacts()
                } else {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.access))
                        .setMessage(getString(R.string.explanation))
                        .setNegativeButton(getString(R.string.close)) { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()

                    binding.getContactsButton.isClickable = true
                }

                return
            }
        }
    }

    private fun getContacts() {
        val contacts = mutableListOf<ContactEntity>()

        val contentResolver: ContentResolver = contentResolver

        val cursorWithContacts: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            "${ContactsContract.Contacts.DISPLAY_NAME} $ORDER"
        )

        cursorWithContacts?.let { cursor ->
            for (i in 0..cursor.count) {
                if (cursor.moveToPosition(i)) {
                    val name =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                    val phoneNumber =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    if (contacts.find { it.name == name } == null) {
                        contacts.add(ContactEntity(name, phoneNumber))
                    }
                }
            }
        }

        cursorWithContacts?.close()

        displayContacts(contacts)
    }

    private fun displayContacts(contacts: List<ContactEntity>) {
        binding.getContactsButton.isVisible = false
        (binding.contactsRecyclerView.adapter as ContactsAdapter).setData(contacts)
    }
}