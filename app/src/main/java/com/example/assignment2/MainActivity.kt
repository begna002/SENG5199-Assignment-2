package com.example.assignment2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.assignment2.ui.theme.Assignment2Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var startDateString by mutableStateOf("")
    private var endDateString by mutableStateOf("")
    private var searchable by mutableStateOf(false)
    private var responseFetched by mutableStateOf(false)
    private var response by mutableStateOf(listOf(ResponseItem("", "", "", false)))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assignment2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column (
                        modifier = Modifier.paint(
                            painterResource(id = R.drawable.starry_background),
                            contentScale = ContentScale.FillBounds)
                    ) {
                        Header()
                        SearchContainer()
                        if (searchable) {
                            BodyContent()
                        } else {
                            DefaultBody()
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun Header(modifier: Modifier = Modifier) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .height(100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Nasa Images",
                fontSize = 40.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    }

    @Composable
    fun DefaultBody(modifier: Modifier = Modifier) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select date range and search!",
                fontSize = 24.sp,
                textAlign = TextAlign.Right,
                color = Color.White
            )
        }
    }

    @Composable
    fun SearchContainer() {
        val startDialog = remember { mutableStateOf(false) }
        val endDialog = remember { mutableStateOf(false) }


        val setStartDateString: (String) -> Unit = { startString ->
            startDateString = startString
        }

        val setEndDateString: (String) -> Unit = { endString ->
            endDateString = endString
        }

        DateSelector(startDialog, endDialog, setStartDateString, setEndDateString)
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Button(
                    onClick = {
                        startDialog.value = true
                    },
                    modifier = Modifier.size(110.dp, 50.dp)
                ) {
                    Text(
                        text = "Start Date",
                        fontSize = 12.sp,
                    )
                }
                Text (
                    text = startDateString,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                Button(
                    onClick = { endDialog.value = true },
                    modifier = Modifier.size(110.dp, 50.dp)
                ) {
                    Text(
                        text = "End Date",
                        fontSize = 12.sp,
                    )
                }
                Text (
                    text = endDateString,
                    modifier = Modifier.padding(top = 8.dp),
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            Button(
                onClick = {
                    searchable = true
                    responseFetched = false
                    response = listOf(ResponseItem("", "", "", false))
                },
                modifier = Modifier.size(110.dp, 50.dp)
            ) {
                Text(
                    text = "Search!",
                    fontSize = 12.sp,
                )
            }
        }
    }

    @Composable
    fun BodyContent() {
        var routine = rememberCoroutineScope()
        val listState = rememberLazyListState()
        val apiKey = stringResource(R.string.api_key);

        if (!responseFetched) {
            LaunchedEffect(key1 = Unit){
                routine.launch {
                    response = getData(apiKey, startDateString, endDateString)
                }
            }
            responseFetched = true
        }

        if (response[0].title != "") {
            ListHeader()
            SortMenu()
            LazyColumn (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, start = 24.dp),
                state = listState
            )
            {
                itemsIndexed(response) { index, item ->
                    LineItem(item)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        } else if (response[0].error) {
            ErrorMessage()
        } else {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            )
            {
                CircularProgressIndicator(
                    Modifier
                        .size(100.dp)
                        .align(alignment = Alignment.CenterHorizontally),
                    strokeWidth = 18.dp
                )
            }
        }
    }

    @Composable
    fun ListHeader(modifier: Modifier = Modifier) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Click image to view full size",
                fontSize = 24.sp,
                textAlign = TextAlign.Right,
                color = Color.White
            )
        }
    }
    @Composable
    fun LineItem (response: ResponseItem) {
        val uriHandler = LocalUriHandler.current

        val imageDetails = "Title: ${response.title}\n" +
                "Date: ${response.date}"



        Row (
            modifier = Modifier.clickable {
                uriHandler.openUri(response.url)
            },
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(response.url)
                        .memoryCacheKey(response.url)
                        .diskCacheKey(response.url)
                        .build()),
                contentDescription = "Translated description of what the image contains",
                modifier = Modifier
                    .size(width = 100.dp, height = 100.dp)
                    .background(Color.Black)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = imageDetails,
                fontSize = 18.sp,
                color = Color.White
            )
        }
    }

    @Composable
    fun SortMenu() {
        var expanded by remember { mutableStateOf(false) }
        var sortOption by remember { mutableStateOf("Sort By") }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { expanded = !expanded }) {
                Text(
                    text = sortOption,
                    fontSize = 12.sp,
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false }
            ) {
                DropdownMenuItem(
                    enabled = sortOption != CommonEnums.DateAsc.string,
                    text = { Text(CommonEnums.DateAsc.string) },
                    onClick = {
                        response = response.sortedBy { it.date }
                        expanded = false
                        sortOption = CommonEnums.DateAsc.string
                    }
                )
                DropdownMenuItem(
                    enabled = sortOption != CommonEnums.DateDesc.string,
                    text = { Text(CommonEnums.DateDesc.string) },
                    onClick = {
                        response = response.sortedByDescending { it.date }
                        expanded = false
                        sortOption = CommonEnums.DateDesc.string
                    }
                )
                DropdownMenuItem(
                    enabled = sortOption != CommonEnums.TitleAZ.string,
                    text = { Text(CommonEnums.TitleAZ.string) },
                    onClick = {
                        response = response.sortedBy { it.title }
                        expanded = false
                        sortOption = CommonEnums.TitleAZ.string
                    }
                )
                DropdownMenuItem(
                    enabled = sortOption != CommonEnums.TitleZA.string,
                    text = { Text(CommonEnums.TitleZA.string) },
                    onClick = {
                        response = response.sortedByDescending { it.title }
                        expanded = false
                        sortOption = CommonEnums.TitleZA.string
                    }
                )
            }
        }
    }

    @Composable
    fun ErrorMessage (modifier: Modifier = Modifier) {
        Column (
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error Fetching Data",
                fontSize = 24.sp,
                textAlign = TextAlign.Right,
                color = Color.White
            )
        }
    }
}

