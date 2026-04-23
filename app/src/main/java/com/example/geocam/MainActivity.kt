package com.example.geocam

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// --- COLORES ---
val AmarilloPastel = Color(0xFFFFF9C4)
val VerdePastel = Color(0xFFC8E6C9)
val VerdeFuerte = Color(0xFF2E7D32)
val RojoUbicacion = Color(0xFFE53935)
val FondoCrema = Color(0xFFF5F5DC)


data class Destino(val nombre: String, val descripcion: String)
data class Negocio(val nombre: String, val categoria: String, val localidad: String, val descripcion: String)

val lasLocalidades = listOf("Bécal", "Nunkiní", "Dzibalché", "Calkiní", "Pomuch", "Tenabo", "Isla Arena", "El Remate")
val lasCategorias = listOf("Locales Artesanales", "Tiendas o Negocios", "Restaurantes", "Hoteles", "Balnearios", "Zonas Arqueológicas", "Centros Religiosos", "Cafeterías", "Transportes")

class MainActivity : ComponentActivity() {

    private val listaMaestraNegocios = mutableStateListOf<Negocio>(
        Negocio("Artesanías 'La Flor de Jipi'", "Locales Artesanales", "Bécal", "Sombreros tejidos a mano en cuevas."),
        Negocio("KMODA CALKINI", "Tiendas o Negocios", "Calkiní", "Ropa y calzado para toda la familia.")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme { // Agregado MaterialTheme para evitar errores de estilo
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        PantallaLogin { rol ->
                            if (rol == "Emprendedor") navController.navigate("panel_control")
                            else navController.navigate("bienvenida/Usuario")
                        }
                    }

                    composable("panel_control") {
                        PantallaPanelControl(
                            misNegocios = listaMaestraNegocios,
                            onNuevoNegocio = { navController.navigate("registro_negocio") },
                            onLogout = { navController.navigate("login") }
                        )
                    }

                    composable("registro_negocio") {
                        PantallaRegistroNegocio { nuevoNegocio ->
                            listaMaestraNegocios.add(nuevoNegocio)
                            navController.navigate("precios")
                        }
                    }

                    composable("precios") {
                        PantallaPlanes { navController.navigate("panel_control") }
                    }

                    composable(
                        "bienvenida/{rol}",
                        arguments = listOf(navArgument("rol") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val rol = backStackEntry.arguments?.getString("rol") ?: "Usuario"
                        PantallaBienvenida(rol) { navController.navigate("lista") }
                    }

                    composable("lista") {
                        PantallaListaLugares { lugar -> navController.navigate("categorias/$lugar") }
                    }

                    composable(
                        "categorias/{lugar}",
                        arguments = listOf(navArgument("lugar") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val lugar = backStackEntry.arguments?.getString("lugar") ?: ""
                        PantallaMenuCategorias(
                            nombreLugar = lugar,
                            onCategoriaClick = { cat -> navController.navigate("negocios/$lugar/$cat") },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(
                        route = "negocios/{lugar}/{categoria}",
                        arguments = listOf(
                            navArgument("lugar") { type = NavType.StringType },
                            navArgument("categoria") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val lugar = backStackEntry.arguments?.getString("lugar") ?: ""
                        val cat = backStackEntry.arguments?.getString("categoria") ?: ""
                        PantallaListadoNegocios(lugar, cat, listaMaestraNegocios) {
                            navController.popBackStack()
                        }
                    }
                }
            }
        }
    }
}

// --- PANEL DE CONTROL ---
@Composable
fun PantallaPanelControl(misNegocios: List<Negocio>, onNuevoNegocio: () -> Unit, onLogout: () -> Unit) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoNegocio,
                containerColor = VerdeFuerte,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text(" Nuevo Negocio")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().background(FondoCrema).padding(padding).padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Mi Panel", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = VerdeFuerte)
                IconButton(onClick = onLogout) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Salir", tint = RojoUbicacion)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = VerdePastel)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Establecimientos: ${misNegocios.size}", fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(misNegocios) { negocio ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        ListItem(
                            headlineContent = { Text(negocio.nombre, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${negocio.localidad} • ${negocio.categoria}") },
                            trailingContent = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = VerdeFuerte) }
                        )
                    }
                }
            }
        }
    }
}

// --- REGISTRO ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroNegocio(onRegistroCompletado: (Negocio) -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoriaSeleccionada by remember { mutableStateOf(lasCategorias[0]) }
    var localidadSeleccionada by remember { mutableStateOf(lasLocalidades[0]) }

    Column(modifier = Modifier.fillMaxSize().background(VerdePastel).padding(20.dp).verticalScroll(rememberScrollState())) {
        Text("Datos del Negocio", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())

        Text("Localidad:", modifier = Modifier.padding(top = 15.dp), fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lasLocalidades) { loc ->
                FilterChip(
                    selected = localidadSeleccionada == loc,
                    onClick = { localidadSeleccionada = loc },
                    label = { Text(loc) }
                )
            }
        }
        Text("Categoría:", modifier = Modifier.padding(top = 10.dp), fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(lasCategorias) { cat ->
                FilterChip(
                    selected = categoriaSeleccionada == cat,
                    onClick = { categoriaSeleccionada = cat },
                    label = { Text(cat) }
                )
            }
        }
        Button(
            onClick = { onRegistroCompletado(Negocio(nombre.trim(), categoriaSeleccionada, localidadSeleccionada, descripcion.trim())) },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
            enabled = nombre.isNotEmpty()
        ) { Text("Pagar y Publicar") }
    }
}

// --- LISTADO (TURISTA) ---
@Composable
fun PantallaListadoNegocios(lugar: String, categoria: String, almacen: List<Negocio>, onBack: () -> Unit) {
    val filtrados = almacen.filter { it.localidad == lugar && it.categoria == categoria }
    Box(modifier = Modifier.fillMaxSize().background(FondoCrema)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(categoria.uppercase(), fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text("Resultados en $lugar", color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filtrados) { neg ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(neg.nombre, fontWeight = FontWeight.Bold)
                                Text(neg.descripcion, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = RojoUbicacion)
                        }
                    }
                }
            }
        }
        FloatingActionButton(
            onClick = onBack,
            modifier = Modifier.align(Alignment.BottomStart).padding(20.dp),
            containerColor = Color(0xFFD84315),
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
        }
    }
}

// --- RESTO DE PANTALLAS ---
@Composable
fun PantallaListaLugares(onLugarClick: (String) -> Unit) {
    val lugares = listOf(
        Destino("Bécal", "Cuna del sombrero de Jipi-Japa."),
        Destino("Nunkiní", "Tierra de los famosos Osos."),
        Destino("Dzibalché", "Riqueza cultural."),
        Destino("Calkiní", "Atenas del Camino Real."),
        Destino("Pomuch", "Famoso pan tradicional."),
        Destino("Tenabo", "Tierra de conservas."),
        Destino("Isla Arena", "Paraíso de flamencos."),
        Destino("El Remate", "Ojo de agua cristalina.")
    )
    Column(modifier = Modifier.fillMaxSize().background(VerdePastel).padding(16.dp)) {
        Text("Explora Campeche", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(lugares) { destino ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onLugarClick(destino.nombre) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(destino.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = VerdeFuerte)
                        Text(destino.descripcion)
                    }
                }
            }
        }
    }
}

@Composable
fun PantallaMenuCategorias(nombreLugar: String, onCategoriaClick: (String) -> Unit, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(AmarilloPastel).padding(16.dp)) {
        Text(text = nombreLugar, fontSize = 32.sp, fontWeight = FontWeight.Bold, color = VerdeFuerte)
        Spacer(modifier = Modifier.height(20.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(lasCategorias) { cat ->
                Card(modifier = Modifier.fillMaxWidth().height(110.dp).clickable { onCategoriaClick(cat) }, colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Text(cat, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Volver") }
    }
}

@Composable
fun PantallaLogin(onLoginSuccess: (String) -> Unit) {
    var uInput by remember { mutableStateOf("") }
    var pInput by remember { mutableStateOf("") }
    var esEmprendedor by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(AmarilloPastel).padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("GeoCam", fontSize = 45.sp, fontWeight = FontWeight.ExtraBold, color = VerdeFuerte)
        Spacer(modifier = Modifier.height(30.dp))
        OutlinedTextField(value = uInput, onValueChange = { uInput = it; errorMsg = "" }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = pInput, onValueChange = { pInput = it; errorMsg = "" }, label = { Text("Contraseña") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        if (errorMsg.isNotEmpty()) Text(text = errorMsg, color = RojoUbicacion, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = !esEmprendedor, onClick = { esEmprendedor = false })
            Text("Turista")
            Spacer(modifier = Modifier.width(10.dp))
            RadioButton(selected = esEmprendedor, onClick = { esEmprendedor = true })
            Text("Emprendedor")
        }
        Button(onClick = {
            if (uInput == "GEOCAM" && pInput == "2026") onLoginSuccess(if(esEmprendedor) "Emprendedor" else "Usuario")
            else errorMsg = "Credenciales incorrectas"
        }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = VerdeFuerte)) { Text("ENTRAR", color = Color.White) }
    }
}

@Composable
fun PantallaPlanes(onAceptar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(AmarilloPastel).padding(28.dp), verticalArrangement = Arrangement.Center) {
        Text("Suscripción", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Button(onClick = onAceptar, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) { Text("$50 MXN / MES") }
        Button(onClick = onAceptar, modifier = Modifier.fillMaxWidth().padding(top = 10.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD84315))) { Text("$500 MXN / AÑO") }
    }
}

@Composable
fun PantallaBienvenida(rol: String, onExplorar: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(AmarilloPastel).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("¡Bienvenido, $rol!", fontSize = 24.sp, color = VerdeFuerte)
        Button(onClick = onExplorar, modifier = Modifier.padding(top = 30.dp)) { Text("CONTINUAR") }
    }
}