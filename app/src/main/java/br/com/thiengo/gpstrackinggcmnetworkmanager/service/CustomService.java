package br.com.thiengo.gpstrackinggcmnetworkmanager.service;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import br.com.thiengo.gpstrackinggcmnetworkmanager.domain.UserTracking;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.solodovnikov.rxlocationmanager.LocationRequestBuilder;
import ru.solodovnikov.rxlocationmanager.LocationTime;
import rx.Subscriber;


public class CustomService extends GcmTaskService {
    private static final String TAG = "log";
    private Location location = null;
    private int isAbleToFinish = 0;


    @Override
    public void onInitializeTasks() {
        super.onInitializeTasks();

        /*
         * NA REINSTALAÇÃO DA APLICAÇÃO OU ATÉ MESMO NO UPDATE DELA, TODAS AS TAREFAS
         * CRIADAS SÃO DESTRUÍDAS, DESSA FORMA É PRECISO INICIALIZA-LAS NOVAMENTE,
         * ESSE MÉTODO É ONDE VOCÊ ACESSA SUA BASE LOCAL (OU REMOTA) E REMOTAR SUAS
         * TAREFAS COM OS DADOS RECUPERADOS DA BASE. NOTE QUE ESSE MÉTODO RODA NA
         * THREAD PRINCIPAL, DIFERENTE DO MÉTODO onRunTask()
        */
    }

    @Override
    public int onRunTask(TaskParams taskParams) {

        retrieveCoordinate();
        lockThreadUntilCoordinate();
        sendCoordinate();

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    private void lockThreadUntilCoordinate(){
        while( isAbleToFinish == 0 ){
            SystemClock.sleep(1000);
        }
    }

    private void retrieveCoordinate() {
        LocationRequestBuilder locationRequestBuilder = new LocationRequestBuilder(getApplicationContext());
        locationRequestBuilder
                .addLastLocation(LocationManager.NETWORK_PROVIDER, new LocationTime(30, TimeUnit.SECONDS), false)
                .addRequestLocation(LocationManager.GPS_PROVIDER, new LocationTime(10, TimeUnit.SECONDS))
                .setDefaultLocation(new Location(LocationManager.PASSIVE_PROVIDER))
                .create().subscribe(new Subscriber<Location>() {
            @Override
            public void onCompleted() {}
            @Override
            public void onError(Throwable e) {}
            @Override
            public void onNext(Location l) {
                location = l;
                isAbleToFinish = 1;
            }
        });
    }

    private void sendCoordinate(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.25.221:8888/gps-tracking-gcm-network-manager/")
                .addConverterFactory( GsonConverterFactory.create() )
                .build();

        UserTracking userTracking = retrofit.create(UserTracking.class);
        Call<String> requester = userTracking.sendCoordinates(
                "user-tracking",
                "sdfsdvsd",
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude())
        );

        String answer = null;
        try {
            answer = requester.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "----> "+answer);
    }
}
