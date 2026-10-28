# AP FUTSAL — versión con competiciones personalizadas

Esta versión conserva la aplicación Android existente y añade gestión local de competiciones:

- Crear y guardar nombres de competiciones desde Ajustes.
- Elegir una competición antes de registrar el partido.
- Mantener todos los partidos en el Historial, con su competición en la columna correspondiente.
- Filtrar las estadísticas generales por competición o ver todas las competiciones.
- Mantener los campos estadísticos y el cronómetro existentes.
- Migrar automáticamente a la lista de competiciones los nombres ya presentes en el partido actual y en el historial.

Los datos siguen almacenándose localmente en la aplicación. Haz una copia de seguridad JSON antes de actualizar y conserva el APK anterior por si necesitas volver a él.

## Compilar con el flujo de GitHub Actions

Sube este ZIP al repositorio y modifica la línea `unzip ...zip` del workflow para usar el nombre de este archivo. El ZIP contiene la carpeta raíz `AP_FUTSAL_Android/`, como el proyecto anterior.
