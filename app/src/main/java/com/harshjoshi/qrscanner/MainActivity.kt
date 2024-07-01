package com.harshjoshi.qrscanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.harshjoshi.qrscanner.ui.theme.QrScannerTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity()
{
    private var textResult = mutableStateOf("Click on the Icon to Scan")

    private val barCodeLauncher = registerForActivityResult(ScanContract())
    { result->
        if(result.contents == null)
        {
            Toast.makeText(this@MainActivity,
                "Operation Cancelled ",
                Toast.LENGTH_SHORT)
                .show()
        }
        else
        {
            textResult.value = result.contents
        }

    }

    private fun showCamera()
    {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setOrientationLocked(true)

        barCodeLauncher.launch(options)
    }

    private val requestPermissionLauncher = registerForActivityResult( ActivityResultContracts.RequestPermission() )
    { isGranted->
        if(isGranted) {
            showCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            QrScannerTheme {
                Surface(modifier = Modifier.fillMaxSize())
                {
                    Scaffold(
                        bottomBar = { MyBottomAppBar(actionFun = {
                            //give the action fun
                            checkCameraPermission(this@MainActivity)
                        }) },
                        topBar = { MyTopAppBar() }
                    ) {pv->
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(pv)
                                .background(color = MaterialTheme.colorScheme.secondaryContainer))
                        {
                            if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT) {
                                Image(
                                    modifier = Modifier
                                        .shadow(
                                            elevation = 15.dp,
                                            shape = RoundedCornerShape(50),
                                        )
                                        .size(250.dp)
                                        .clip(RoundedCornerShape(50))
                                        .clickable {
                                            checkCameraPermission(this@MainActivity)
                                        },
                                    painter = painterResource(id = R.drawable.scanner_drawable1),
                                    contentDescription = null
                                )
                            }

                            Spacer(modifier = Modifier.size(24.dp))

                            Text(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                text = textResult.value)
                            
                            if(URLUtil.isValidUrl(textResult.value)){
                                Button(onClick = {
                                    this@MainActivity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(textResult.value)))
                                }) {
                                    Text(text = "Open the link")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCameraPermission(context: MainActivity) {
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
        {
            showCamera()
        }
        else if(shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
        {
            Toast.makeText(context, "Camera permission required!", Toast.LENGTH_SHORT).show()
        }
        else
        {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}

@Composable
fun MyBottomAppBar(actionFun: ()->Unit)
{
    BottomAppBar(
        floatingActionButton = {
           FloatingActionButton(onClick = actionFun) {
               Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
           }
        },
        actions = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar()
{
    CenterAlignedTopAppBar(
        title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(modifier = Modifier.size(32.dp),
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null)
                Spacer(modifier = Modifier.size(16.dp))
                Text(text = "QR Code Scanner",
                    fontWeight = FontWeight.Bold)
            }
        }
    )
}