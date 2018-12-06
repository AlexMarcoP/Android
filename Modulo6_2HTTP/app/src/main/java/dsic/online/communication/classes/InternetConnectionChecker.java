package dsic.online.communication.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetConnectionChecker {

    public boolean checkInternetConnection(Context contexto) {

        // 1. Necesitamos acceder al gestor de conectividad de la plataforma
        //    Para ello creamos un objeto de tipo ConnectivityManager
        //    Este objeto nos proporcionará información sobre el tipo de
        //    conexión y de red de la que dispongamos

        ConnectivityManager _gestorDeConectividad =
                (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        //2. Comprobar si el servicio es funcional
        if (_gestorDeConectividad != null) {

            // 3. Obtener información sobre el tipo de conexión requerido
            //    Los tipos disponibles pueden consultarse en
            //    http://developer.android.com/reference/android/net/ConnectivityManager.html

            NetworkInfo info3G = _gestorDeConectividad.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo infoWIFI = _gestorDeConectividad.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            // 4 ¿Conexión a Internet vía WIFI ?

            if ((infoWIFI != null) &&
                    infoWIFI.isAvailable() &&
                    (infoWIFI.getDetailedState() == NetworkInfo.DetailedState.CONNECTED)) {
                return true;
            }

            // 5. ¿Conexión a Internet vía 3G/4G?
            if ((info3G != null) &&
                    info3G.isAvailable() &&
                    (info3G.getDetailedState() == NetworkInfo.DetailedState.CONNECTED)) {
                return true;
            }

            //6. No hay conexión disponible
            return false;

        }
        return false;
    }
}
