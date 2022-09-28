package com.nmwilkinson.mediastorefiles

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.nmwilkinson.mediastorefiles.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var filesAdapter: FilesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        filesAdapter = FilesAdapter(this)
        binding.list.adapter = filesAdapter
        binding.list.layoutManager = LinearLayoutManager(this, VERTICAL, false)
        binding.create.setOnClickListener {
            filesAdapter.createFile()
            filesAdapter.scanDirectory()
        }
        binding.scan.setOnClickListener { filesAdapter.scanDirectory() }
    }

    override fun onResume() {
        super.onResume()
        filesAdapter.scanDirectory()
    }
}

