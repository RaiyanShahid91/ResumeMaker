package app.craft.myresume.ads.admob;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import app.craft.myresume.R;
import app.craft.myresume.ads.MyApplication;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

public class AdMobInterstitialClick {

    static int gclick = 1;
    private static AdMobInterstitialClick mInstance;
    public InterstitialAd interstitialOne;
    public InterstitialAd interstitialThree;
    public InterstitialAd interstitialTwo;
    MyCallback myCallback;

    public interface MyCallback {
        void callbackCall();
    }

    public static AdMobInterstitialClick getInstance() {
        if (mInstance == null) {
            mInstance = new AdMobInterstitialClick();
        }
        return mInstance;
    }

    public void loadInterOne(final Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        InterstitialAd.load(activity, MyApplication.AdMob_Int, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClick.this.interstitialOne = interstitialAd;
                Log.i("TAG", "onAdLoaded 1");
                AdMobInterstitialClick.this.interstitialOne.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClick.this.interstitialOne = null;
                        Log.d("TAG", "The ad 1 was dismissed.");


                        AdMobInterstitialClick.this.loadInterOne(activity);

                        if (AdMobInterstitialClick.this.myCallback != null) {
                            AdMobInterstitialClick.this.myCallback.callbackCall();
                            AdMobInterstitialClick.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClick.this.interstitialOne = null;
                        Log.d("TAG", "The ad 1 failed to show.");
                        AdMobInterstitialClick.this.loadInterOne(activity);

                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 1 Load Error.");
                AdMobInterstitialClick.this.interstitialOne = null;


            }
        });
    }

    public void loadInterTwo(final Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        InterstitialAd.load(activity, MyApplication.AdMob_Int, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClick.this.interstitialTwo = interstitialAd;
                Log.i("TAG", "onAdLoaded 2");
                AdMobInterstitialClick.this.interstitialTwo.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClick.this.interstitialTwo = null;
                        Log.d("TAG", "The ad 2 was dismissed.");
                        AdMobInterstitialClick.this.loadInterTwo(activity);
                        if (AdMobInterstitialClick.this.myCallback != null) {
                            AdMobInterstitialClick.this.myCallback.callbackCall();
                            AdMobInterstitialClick.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClick.this.interstitialTwo = null;
                        Log.d("TAG", "The ad 2 failed to show.");
                        AdMobInterstitialClick.this.loadInterTwo(activity);


                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 2 Load Error.");
                AdMobInterstitialClick.this.interstitialTwo = null;

//                activity.startActivity(new Intent(activity, FailedInterstitial.class));
//                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);
            }
        });
    }

    public void loadInterThree(final Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        InterstitialAd.load(activity, MyApplication.AdMob_Int, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClick.this.interstitialThree = interstitialAd;
                Log.i("TAG", "onAdLoaded 2");
                AdMobInterstitialClick.this.interstitialThree.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClick.this.interstitialThree = null;
                        Log.d("TAG", "The ad 2 was dismissed.");
                        AdMobInterstitialClick.this.loadInterThree(activity);
                        if (AdMobInterstitialClick.this.myCallback != null) {
                            AdMobInterstitialClick.this.myCallback.callbackCall();
                            AdMobInterstitialClick.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClick.this.interstitialThree = null;
                        Log.d("TAG", "The ad 2 failed to show.");
                        AdMobInterstitialClick.this.loadInterThree(activity);


                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 2 Load Error.");
                AdMobInterstitialClick.this.interstitialThree = null;


//                activity.startActivity(new Intent(activity, FailedInterstitial.class));
//                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);

            }
        });
    }

    public void showInter(Activity activity, MyCallback myCallback2) {

        MyApplication.click1++;

        if (MyApplication.click1 == MyApplication.click) {

            MyApplication.click1 = 0;

            InterstitialAd interstitialAd = this.interstitialOne;

            if (interstitialAd != null) {
                interstitialAd.show(activity);
                return;
            } /*else {
                activity.startActivity(new Intent(activity, FailedInterstitial.class));
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);
                return;
            }*/

            InterstitialAd interstitialAd2 = this.interstitialTwo;
            if (interstitialAd2 != null) {
                interstitialAd2.show(activity);
                return;
            } /*else {
                activity.startActivity(new Intent(activity, FailedInterstitial.class));
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);
                return;
            }*/


            InterstitialAd interstitialAd3 = this.interstitialThree;
            if (interstitialAd3 != null) {
                interstitialAd3.show(activity);
//                return;
            } else {
                //activity.startActivity(new Intent(activity, FailedInterstitial.class));
                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);
//                return;
            }


            MyCallback myCallback3 = this.myCallback;
            if (myCallback3 != null) {
                myCallback3.callbackCall();
                this.myCallback = null;
                return;
            }
            return;
        }else{


        }
//        gclick = i + 1;
        MyCallback myCallback4 = this.myCallback;
        if (myCallback4 != null) {
            myCallback4.callbackCall();
            this.myCallback = null;
        }
    }

    public boolean isInternetOn(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        if (connectivityManager.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED || connectivityManager.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED) {
        }
        return false;
    }

    public static void alert(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Internet Alert");
        builder.setMessage("You need to internet connection");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }

}
