package com.muhamaddzikri0103.bookshelf.ui.screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.muhamaddzikri0103.bookshelf.R
import com.muhamaddzikri0103.bookshelf.model.BookAndReading
import com.muhamaddzikri0103.bookshelf.navigation.Screen
import com.muhamaddzikri0103.bookshelf.util.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val READING_DETAIL_KEY_ID = "readingDetailId"

private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
private val outputFormatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavHostController, id: Long) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val viewModel: UpsertViewModel = viewModel(factory = factory)

    val data by viewModel.getBookAndReadingById(id).collectAsState(initial = null)
    if (data == null) return

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.reading_detail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                actions = {
                    UpdateNDelete(navController, id) {
                        viewModel.softDelete(id)
                        navController.popBackStack()
                        Toast.makeText(context, R.string.toast_move, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { innerPadding ->
        ReadingDetail(data!!, viewModel, modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun UpdateNDelete(navController: NavHostController, id: Long, onMoveClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    IconButton(onClick = { expanded = true }) {
        Icon(
            imageVector = Icons.Filled.MoreVert,
            contentDescription = stringResource(R.string.other),
            tint = MaterialTheme.colorScheme.primary
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.edit_book))
                },
                onClick = {
                    expanded = false
                    navController.navigate(Screen.UpdateForm.withId(id))
                }
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(R.string.move_trash))
                },
                onClick = {
                    expanded = false
                    onMoveClick()
                }
            )
        }
    }

}

@Composable
fun ReadingDetail(data: BookAndReading, viewModel: UpsertViewModel, modifier: Modifier = Modifier) {
    val title = data.title
    val author = data.author
    val genre = data.genre
    val numOfPages = data.numOfPages
    var dateModified by remember { mutableStateOf(data.dateModified) }
    var currentPage by remember { mutableIntStateOf(data.currentPage) }

    val pagesLeft: Int = numOfPages - currentPage
    val pct: Double = (currentPage.toDouble() / numOfPages.toDouble()) * 100
    val pctFormat = String.format(Locale.US, "%.0f", pct)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Start
            )
            HorizontalDivider()
            Text(
                text = stringResource(R.string.author_x, author),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.genre_x, genre),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.total_pages_x, numOfPages.toString()),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.x_left, pagesLeft.toString()),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
            Text(
                text = stringResource(R.string.x_completed, pctFormat),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start
            )

            val formattedDate = try {
                val parsedDate = formatter.parse(dateModified)
                outputFormatter.format(parsedDate!!)
            } catch (e: Exception) {
                dateModified
            }
            Text(
                text = stringResource(R.string.last_updated_x, formattedDate),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Start
            )
            ButtonNCounter(data, pagesLeft, viewModel, onProgressUpdate = {
                currentPage = it
            }, onDateModifiedUpdate = {
                dateModified = it
            })
        }
    }
}

@Composable
fun ButtonNCounter(
    data: BookAndReading,
    pagesLeft: Int,
    viewModel: UpsertViewModel,
    onProgressUpdate: (Int) -> Unit,
    onDateModifiedUpdate: (String) -> Unit
) {
    var currentPage by remember { mutableIntStateOf(data.currentPage) }
    var isClicked by remember { mutableStateOf(false) }
    var amount by remember { mutableIntStateOf(0) }
    var totalPagesRead by remember { mutableIntStateOf(0) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            onClick = { isClicked = !isClicked },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = stringResource(R.string.update_progress))
        }

        if (isClicked) {
            Column(modifier = Modifier.padding(top = 16.dp, bottom = 5.dp)) {
                Text(text = stringResource(R.string.pages_read))
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (amount != 0) {
                            amount--
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.baseline_remove_24),
                        contentDescription = stringResource(R.string.minus),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Text(
                    text = amount.toString(),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                IconButton(
                    onClick = {
                        if (amount >= 0 && amount < pagesLeft) {
                            amount++
                        }
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = stringResource(R.string.add),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(30.dp)
                    )
                }

            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        isClicked = false
                        totalPagesRead = currentPage + amount
                        amount = 0
                        val formattedNow = formatter.format(Date())

                        viewModel.update(
                            bookId = data.bookId,
                            title = data.title,
                            author = data.author,
                            genre = data.genre,
                            numOfPages = data.numOfPages.toString(),
                            readingId = data.readingId,
                            currentPage = totalPagesRead.toString(),
                            dateModified = formattedNow
                        )

                        currentPage = totalPagesRead
                        onProgressUpdate(totalPagesRead)
                        onDateModifiedUpdate(formattedNow)
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .padding(4.dp)

                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = stringResource(R.string.add_page),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ReadingDetailPreview() {
    DetailScreen(rememberNavController(), 1)
}
