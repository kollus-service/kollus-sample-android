package kollus.test.media.ui.fragment;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ads.AdsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.kollus.sdk.media2.exoplayer2.source.hls.HlsMediaSource;

import kollus.test.media.R;

import static com.google.ads.interactivemedia.v3.internal.hu.C;

public class ExoPlayerFragment extends BaseFragment {

    private PlayerView playerView;
    private SimpleExoPlayer player;
    private ImaAdsLoader adsLoader;
    private String mp4Url = "http://ikwonseo.video.kr.kollus.com/kr/media-file.mp4?_s=c627eef4a52eb3a8383bcf723c6d582d85c770d18b360e99911865f4c816b07bfd9aa6fafc27f67d150510f8f53665054765b75d358d6376990f49c48346d9fecc9bffd7c5b4fab4974af89991abcb396503d3a304adf54f2f96c88b056dfdf23439271b59c19aa66e129253e7606dfd6bd0c234cb07ed1c8ebb3d678225431688098f88eba2387686b9b1113d958504d2374cf262bf1bab3d6dfbe5a0cdad1be840ddbffd1852ce775be8ec112a23912c7bae1b6987520a7ce4a201ec86279b3f64db0bb36b45a8eeee71a4a93cc5f9b314fd4e0576e92791eaa28639bea21a1c5dfa0d0fe1971ac52d10c72199d1e37741c465c6394e325f21110c4c5dc7075488b6c07ebaccffa483b04307cdb7800e2bfc2b23e57e696955d53f44cfa05576dab7992c62d1d1598775cce2cb2d8fe1ebd54a4148a243297872abc37250cb01a3a8f521b570a43a6d66ed6a232c561e7a41416f51036af1b81ed59beb3eee";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_exo, container, false);
        playerView = root.findViewById(R.id.player_view);
        //adsLoader = new ImaAdsLoader(this, AD_TAG_URI);

        playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        player = ExoPlayerFactory.newSimpleInstance(getContext());
        playerView.setPlayer(player);
        //adsLoader.setPlayer(player);

        MediaSource mediaSource = buildMediaSource(Uri.parse(mp4Url));

        player.prepare(mediaSource, true, false);
        player.setPlayWhenReady(true);

        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        //adsLoader.setPlayer(null);
        //playerView.setPlayer(null);
        //player.release();
        //player = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playerView.setPlayer(null);
        player.release();
        player = null;
    }

    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = Util.getUserAgent(getContext(), "blackJin");
        return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);
    }

    /*
    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = Util.getUserAgent(this, "blackJin");

        if (uri.getLastPathSegment().contains("mp3") || uri.getLastPathSegment().contains("mp4")) {

            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

        } else if (uri.getLastPathSegment().contains("m3u8")) {

            //com.google.android.exoplayer:exoplayer-hls 확장 라이브러리를 빌드 해야 합니다.
            return new HlsMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);

        } else {

            return new ExtractorMediaSource.Factory(new DefaultDataSourceFactory(this, userAgent))
                    .createMediaSource(uri);
        }

    }
    */
}
