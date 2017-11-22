package com.example.android.assignment_06;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {
    TextView textview;
    Button button_play;
    Document doc = null;
    LinearLayout layout = null;
    String weather = null;
    MediaPlayer mp = null;
    SeekBar seekBar;
    TextView seektext;
    TextView mediatext;
    Handler seekHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xmlparsing_dom);
        GetXMLTask task = new GetXMLTask();
        task.execute("http://www.kma.go.kr/wid/queryDFSRSS.jsp?zone=1159060500");



        textview = (TextView) findViewById(R.id.textView1);
        button_play = (Button) findViewById(R.id.button_play);
        button_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null){
                    IsPlaying();
                }else {
                    IsPlaying();
                }
            }
            private void IsPlaying(){
                if(!mp.isPlaying()){
                    try{
                        mp.prepare();
                    }catch(IllegalStateException e){
                        e.printStackTrace();
                    }catch(IOException e){
                        e.printStackTrace();
                    }
                    mp.start();
                    seekHandler.postDelayed(run, 1000);
                    button_play.setText("PAUSE");
                }else{
                    button_play.setText("PLAY");
                    mp.pause();
                }
            }
        });

        seektext = (TextView)findViewById(R.id.seektext);


    }
    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (mp.isPlaying()) {
                int mediaPos_new = mp.getCurrentPosition();
                int mediaMax_new = mp.getDuration();

                seekBar.setMax(mediaMax_new);
                seekBar.setProgress(mediaPos_new);

                seekHandler.postDelayed(run, 1000);
            }
        }
    };


    //private inner class extending AsyncTask
    private class GetXMLTask extends AsyncTask<String, Void, Document> {
        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL(urls[0]);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder(); //XML문서 빌더 객체를 생성
                doc = db.parse(new InputSource(url.openStream())); //XML문서를 파싱한다.
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                Toast.makeText(getBaseContext(), "Parsing Error", Toast.LENGTH_SHORT).show();
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            String s = "";
            //data태그가 있는 노드를 찾아서 리스트 형태로 만들어서 반환
            NodeList nodeList = doc.getElementsByTagName("data");
            //data 태그를 가지는 노드를 찾음, 계층적인 노드 구조를 반환

            DateFormat dfHH = new SimpleDateFormat("HH", java.util.Locale.getDefault());
            DateFormat dfmm = new SimpleDateFormat("mm", java.util.Locale.getDefault());
            Date dateHH = new Date();
            Date datemm = new Date();
            String sdfHH = dfHH.format(dateHH);
            String sdfmm = dfmm.format(datemm);
            int isdfHH = (Integer.parseInt(sdfHH)+9)%24;
            s += "현재시간 "+isdfHH+"시 "+sdfmm+"분 입니다";
            s += "   날씨 정보: ";

            Node node = nodeList.item(0);
            Element fstElmnt = (Element) node;
            NodeList nameList  = fstElmnt.getElementsByTagName("temp");
            NodeList websiteList = fstElmnt.getElementsByTagName("wfKor");

            Element nameElement = (Element) nameList.item(0);
            nameList = nameElement.getChildNodes();
            s += "온도 = "+ ((Node) nameList.item(0)).getNodeValue() +" ,";

            //<wfKor>맑음</wfKor> =====> <wfKor> 태그의 첫번째 자식노드는 TextNode 이고 TextNode의 값은 맑음
            weather = websiteList.item(0).getChildNodes().item(0).getNodeValue();
            s += "날씨 = "+  weather +"\n";
           long r = System.currentTimeMillis();
            if(weather.equals("비&눈")){
                mp = MediaPlayer.create(MainActivity.this, R.raw.snow_rainy);
            }else if(weather.equals("구름 조금")){
                if(r%2==1){
                    mp = MediaPlayer.create(MainActivity.this, R.raw.littlecloud1);
                }else{
                    mp = MediaPlayer.create(MainActivity.this, R.raw.littlecloud2);
                }
            }else if(weather.equals("구름 많음")){
                if(r%2==1){
                    mp = MediaPlayer.create(MainActivity.this, R.raw.bigcloud1);
                }else{
                    mp = MediaPlayer.create(MainActivity.this, R.raw.bigcloud2);
                }
            }else if(weather.equals("흐림")){
                if(r%2==1){
                    mp = MediaPlayer.create(MainActivity.this, R.raw.cloudy1);
                }else{
                    mp = MediaPlayer.create(MainActivity.this, R.raw.cloudy2);
                }
            }else if(weather.equals("눈")){
                if(r%2==1){
                    mp = MediaPlayer.create(MainActivity.this, R.raw.snow1);
                }else{
                    mp = MediaPlayer.create(MainActivity.this, R.raw.snow2);
                }
            }else{
                if(r%2==1){
                    mp = MediaPlayer.create(MainActivity.this, R.raw.sunny1);
                }else{
                    mp = MediaPlayer.create(MainActivity.this, R.raw.sunny2);
                }
            }
            mediatext = (TextView)findViewById(R.id.mediatext);
            mediatext.setText(weather);
            textview.setText(s);

            seekBar = (SeekBar)findViewById(R.id.seekBar);
            seekBar.setVisibility(ProgressBar.VISIBLE);
            seekBar.setMax(mp.getDuration());
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) {
                        mp.seekTo(progress);
                    }
                    int m = progress / 60000;
                    int s = (progress % 60000) / 1000;
                    String strTime = String.format("%02d:%02d", m, s);
                    seektext.setText(strTime);
                }
                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }
                @Override public void onStopTrackingTouch(SeekBar seekBar) { }
            });
            super.onPostExecute(doc);
        }
    }//end inner class - GetXMLTask
}
