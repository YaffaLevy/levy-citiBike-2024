package service;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class LambdaServiceFactory {

    public LambdaService getService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://wisgvutuqfkvthmaw7mlwgrk440lzwbo.lambda-url.us-east-2.on.aws/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();

        return retrofit.create(LambdaService.class);
    }
}
