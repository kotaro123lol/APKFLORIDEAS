# Codigo de la API
```php
<?php
if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    header("HTTP/1.1 200 OK");
    $con = mysqli_connect("localhost", "root", "", "bdproductos");
    $response = array();
    if ($con) {
        if (isset($_GET['id'])) {
            $id = intval($_GET['id']);
            $sql = "SELECT * FROM productos WHERE ProductoID = $id";
        } elseif (isset($_GET['nombre'])) {
            $nombre = mysqli_real_escape_string($con, $_GET['nombre']);
            $sql = "SELECT * FROM productos WHERE Nombre LIKE '%$nombre%'";
        } else {
            $sql = "SELECT * FROM productos";
        }
        
        $result = mysqli_query($con, $sql);
        
        if ($result) {
            header("Content-Type: application/json");
            if (isset($_GET['id'])) {
                $row = mysqli_fetch_assoc($result);
                if ($row) {
                    $response['ProductoID'] = $row['ProductoID'];
                    $response['Nombre'] = $row['Nombre'];
                    $response['Descripcion'] = $row['Descripcion'];
                    $response['Precio'] = $row['Precio'];
                    $response['Stock'] = $row['Stock'];
                    $response['RutaImagen'] = $row['RutaImagen'];
                } else {
                    $response['error'] = "Producto no encontrado";
                }
            } else {
                $i = 0; 
                while ($row = mysqli_fetch_assoc($result)) {
                    $response[$i]['ProductoID'] = $row['ProductoID'];
                    $response[$i]['Nombre'] = $row['Nombre'];
                    $response[$i]['Descripcion'] = $row['Descripcion'];
                    $response[$i]['Precio'] = $row['Precio'];
                    $response[$i]['Stock'] = $row['Stock'];
                    $response[$i]['RutaImagen'] = $row['RutaImagen'];
                    $i++; 
                }
            }
            echo json_encode($response, JSON_PRETTY_PRINT);
        }
    } else {
        echo "Connection Failed";
    }
} elseif ($_SERVER['REQUEST_METHOD'] === 'POST') {
    header("HTTP/1.1 200 OK");
    $con = mysqli_connect("localhost", "root", "", "bdproductos");
    if ($con) {
        $data = json_decode(file_get_contents("php://input"), true);
        $nombre = mysqli_real_escape_string($con, $data['Nombre']);
        $descripcion = mysqli_real_escape_string($con, $data['Descripcion']);
        $precio = floatval($data['Precio']);
        $stock = intval($data['Stock']);
        $rutaImagen = mysqli_real_escape_string($con, $data['RutaImagen']);
        
        $sql = "INSERT INTO productos (Nombre, Descripcion, Precio, Stock, RutaImagen) VALUES ('$nombre', '$descripcion', $precio, $stock, '$rutaImagen')";
        
        if (mysqli_query($con, $sql)) {
            $response = array('status' => 'success', 'message' => 'Producto agregado correctamente');
        } else {
            $response = array('status' => 'error', 'message' => 'Error al agregar el producto');
        }
        echo json_encode($response, JSON_PRETTY_PRINT);
    } else {
        echo "Connection Failed";
    }
} else {
    header("HTTP/1.1 405 Method Not Allowed");
    echo "Only GET and POST methods are allowed";
}
?>
```
