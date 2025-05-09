package com.muhamaddzikri0103.bookshelf.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.muhamaddzikri0103.bookshelf.ui.screen.DetailScreen
import com.muhamaddzikri0103.bookshelf.ui.screen.MainScreen
import com.muhamaddzikri0103.bookshelf.ui.screen.READING_DETAIL_KEY_ID
import com.muhamaddzikri0103.bookshelf.ui.screen.TrashScreen
import com.muhamaddzikri0103.bookshelf.ui.screen.UpsertScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
   NavHost(
       navController = navController,
       startDestination = Screen.Home.route
   ) {
       composable(route = Screen.Home.route) {
           MainScreen(navController)
       }
       composable(route = Screen.InsertForm.route) {
           UpsertScreen(navController)
       }
       composable(
           route = Screen.DetailScreen.route,
           arguments = listOf(
               navArgument(READING_DETAIL_KEY_ID) { type = NavType.LongType }
           )
       ) { navBackStackEntry ->
           val id = navBackStackEntry.arguments?.getLong(READING_DETAIL_KEY_ID) ?: return@composable
           DetailScreen(navController, id)
       }
       composable(
           route = Screen.UpdateForm.route,
           arguments = listOf(
               navArgument(READING_DETAIL_KEY_ID) { type = NavType.LongType }
           )
       ) { navBackStackEntry ->
           val id = navBackStackEntry.arguments?.getLong(READING_DETAIL_KEY_ID)
           UpsertScreen(navController, id)

       }
       composable(route = Screen.TrashScreen.route) {
           TrashScreen(navController)
       }
   }
}