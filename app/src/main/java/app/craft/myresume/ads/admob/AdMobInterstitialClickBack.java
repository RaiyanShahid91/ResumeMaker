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

public class AdMobInterstitialClickBack {
    static int gclick = 0;
    private static AdMobInterstitialClickBack mInstance;
    public InterstitialAd interstitialOne;
    public InterstitialAd interstitialThree;
    public InterstitialAd interstitialTwo;
    MyCallback myCallback;

    public interface MyCallback {
        void callbackCall();
    }

    public static AdMobInterstitialClickBack getInstance() {
        if (mInstance == null) {
            mInstance = new AdMobInterstitialClickBack();
        }
        return mInstance;
    }

    public void loadInterOne(final Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        InterstitialAd.load(activity, MyApplication.AdMob_Int2, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClickBack.this.interstitialOne = interstitialAd;
                Log.i("TAG", "onAdLoaded 1");
                AdMobInterstitialClickBack.this.interstitialOne.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClickBack.this.interstitialOne = null;
                        Log.d("TAG", "The ad 1 was dismissed.");


                        AdMobInterstitialClickBack.this.loadInterOne(activity);

                        if (AdMobInterstitialClickBack.this.myCallback != null) {
                            AdMobInterstitialClickBack.this.myCallback.callbackCall();
                            AdMobInterstitialClickBack.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClickBack.this.interstitialOne = null;
                        Log.d("TAG", "The ad 1 failed to show.");
                        AdMobInterstitialClickBack.this.loadInterOne(activity);


                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 1 Load Error.");
                AdMobInterstitialClickBack.this.interstitialOne = null;


            }
        });
    }

    public void loadInterTwo(final Activity activity) {
        MobileAds.initialize(activity, new OnInitializationCompleteListener() {
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        InterstitialAd.load(activity, MyApplication.AdMob_Int2, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClickBack.this.interstitialTwo = interstitialAd;
                Log.i("TAG", "onAdLoaded 2");
                AdMobInterstitialClickBack.this.interstitialTwo.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClickBack.this.interstitialTwo = null;
                        Log.d("TAG", "The ad 2 was dismissed.");
                        AdMobInterstitialClickBack.this.loadInterTwo(activity);
                        if (AdMobInterstitialClickBack.this.myCallback != null) {
                            AdMobInterstitialClickBack.this.myCallback.callbackCall();
                            AdMobInterstitialClickBack.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClickBack.this.interstitialTwo = null;
                        Log.d("TAG", "The ad 2 failed to show.");
                        AdMobInterstitialClickBack.this.loadInterTwo(activity);


                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 2 Load Error.");
                AdMobInterstitialClickBack.this.interstitialTwo = null;

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
        InterstitialAd.load(activity, MyApplication.AdMob_Int2, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            public void onAdLoaded(InterstitialAd interstitialAd) {
                AdMobInterstitialClickBack.this.interstitialThree = interstitialAd;
                Log.i("TAG", "onAdLoaded 2");
                AdMobInterstitialClickBack.this.interstitialThree.setFullScreenContentCallback(new FullScreenContentCallback() {
                    public void onAdDismissedFullScreenContent() {
                        AdMobInterstitialClickBack.this.interstitialThree = null;
                        Log.d("TAG", "The ad 2 was dismissed.");
                        AdMobInterstitialClickBack.this.loadInterThree(activity);
                        if (AdMobInterstitialClickBack.this.myCallback != null) {
                            AdMobInterstitialClickBack.this.myCallback.callbackCall();
                            AdMobInterstitialClickBack.this.myCallback = null;
                        }
                    }

                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                        AdMobInterstitialClickBack.this.interstitialThree = null;
                        Log.d("TAG", "The ad 2 failed to show.");
                        AdMobInterstitialClickBack.this.loadInterThree(activity);


                    }
                });
            }

            public void onAdFailedToLoad(LoadAdError loadAdError) {
                Log.d("TAG", "The ad 2 Load Error.");
                AdMobInterstitialClickBack.this.interstitialThree = null;

//                activity.startActivity(new Intent(activity, FailedInterstitial.class));
//                activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);

            }
        });
    }

    public void showInter(Activity activity, MyCallback myCallback2) {

        gclick++;

        if (gclick == MyApplication.backclick) {
            gclick = 0;
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
        } else {
            //activity.startActivity(new Intent(activity, FailedInterstitial.class));
            activity.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fadeout);
        }

        MyCallback myCallback4 = this.myCallback;
        if (myCallback4 != null) {
            myCallback4.callbackCall();
            this.myCallback = null;
        }
    }

    public void showInter2(Activity activity, MyCallback myCallback2) {
        this.myCallback = myCallback2;
        int integer = AdMobSharedPref.getInteger(activity, AdMobSharedPref.CLICK, 1);
        int i = gclick;
        if (i == integer) {
            gclick = 1;

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
        }
        gclick = i + 1;
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
