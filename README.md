Titulo: App insecto

Concepto:
Tiene un boton que al hacer click, consigue informacion de un insecto aleatoriamente
Puedes salvar a los insectos que te hayan gustado y volverlos a visitar

Constraints:

Solamente se pueden realizar 5 consultas al dia y solamente se recargan todas en  el siguiente dia



Tareas a realizar:


1. Primera pantalla GUI
    Creaar los widgets necesarios para que el usuario pueda utilizar la aplicacion.
    Ejemplo: El boton, elemento imagen, elementos para mostrar datos cientificos del insecto,lista para mostrar los url encontrados del insecto,boton que te permite guardar el insecto,boton de configuracion

    El trabajo de esta parte es solamente crear los widgets, tener un event emitter cuando se interactua con el gui, para que otras
    clases puedan reaccionar. Tambien tiene que esperar por la informacion del insecto e insertarla dinamicamente en la pantalla.

    Bloquear el boton cuando las consultas disponibles se agota

    *El estilo de los colores es para otra tarea 


2. Pantalla configuracion
    Implementar widget dark mode y light mode
    Crear los colores de la aplicacion light y dark
    Un toggle que permite dinamicamente cambiar de modo de la aplicacion
    Investigar que colores podria ir bien para la app de insectos


3. Servicio para integrar la API https://www.inaturalist.org/pages/api+reference
   Clase que se encarga de realizar las peticiones y conseguir info de un insecto aleatoriamente
   Regresa la info necesaria en json que se va a mostar en la pantalla principal
   Filtra la busqueda por whitelist y blacklist

   Se encarga de mantener las consultas disponibles y  no se si el timer vaya en otro punto para restablecer las consultas
   Como la API que vamos a utilizar es gratutita, hay que agregar un limite de consultas que se puede realizar al dia


4. Pantalla para mostrar los insectos guardados
    Escoger entre mostrar una lista manera vertical, horizontal o vista grid (Ya es a decision del diseñador)
    Cada objecto que se muestre tiene que tener un id que lo identique en la base sqlite3

    Por cado objecto agregar un boton para eliminar, y luego un popup que muestra confirmar

    Sugeriencia: Cada objecto podria mostrar el nombre de la especie e la imagen del insecto
    Que sucede si no hay imagen del insecto? Se mostraia un imagen que muestre que no hay imagen del insecto


5. Bases de datos
    Diseñar el schema de la base de datos
    Agregar funciones que permitan consultar, agregar,borrar  de la base de datos
    Agregar funcion que permita marcar categorias de insectos como ignorar para la funcionalidad de la blacklist
    (No estoy seguro si sea necesario la opcion de editar ya que la info se consigue de la API)


6. blacklist
    **La seleccion de las categorias se tendria que ver en la API para ver con cuales se puede filtrar**
    Pantalla con un widget que permite agrgar, eliminar categorias de insectos que el usuario no quiere ver

    






