package com.example.madahin.pepe_media;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity {

    private ListView m_musicListView;
    private ListView m_videoListView;

    private final String XML_FILE = "media_list.xml";
    private File m_mediaListXML;
    private List<MediaInfo> m_mediaLists;
    private Dictionary<String, String> m_mediaAccessDict;

    private AsyncTask<File, Integer, Integer> m_refreshMediaList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        // create the TabHost that will contain the Tabs
        TabHost tabHost = (TabHost)findViewById(R.id.tabhost);
        tabHost.setup();


        TabHost.TabSpec tab1 = tabHost.newTabSpec("First Tab");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("Second Tab");


        // Set the Tab name and Activity
        // that will be opened when particular Tab will be selected
        tab1.setIndicator("Musiques");
        tab1.setContent(R.id.tab1);

        tab2.setIndicator("Vidéos");
        tab2.setContent(R.id.tab2);


        /** Add the tabs  to the TabHost to display. */
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);

        m_musicListView = (ListView) findViewById(R.id.music_layout);

        m_musicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, MediaActivity.class);
                intent.putExtra("media_path", m_mediaAccessDict.get(m_musicListView.getItemAtPosition(position)));
                startActivity(intent);


            }
        });

        m_videoListView = (ListView) findViewById(R.id.video_layout);

        m_videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, MediaActivity.class);
                intent.putExtra("media_path", m_mediaAccessDict.get(m_videoListView.getItemAtPosition(position)));
                startActivity(intent);


            }
        });

        m_mediaAccessDict = new Hashtable<>();
        m_mediaListXML = new File(getApplicationContext().getFilesDir(), XML_FILE);

        m_mediaLists = new ArrayList<>();

        resetUpdateAsync();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(m_mediaListXML.exists()){
            loadXML();
            populateMediaList();
        }

        refreshMediaList();
    }

    @Override
    protected  void onStop(){
        super.onStop();

        saveXML();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_refresh:
                refreshMediaList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshMediaList()
    {
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if( m_refreshMediaList.getStatus() == AsyncTask.Status.FINISHED){
            resetUpdateAsync();
        }

        if(permissionCheck == PackageManager.PERMISSION_GRANTED && m_refreshMediaList.getStatus() == AsyncTask.Status.PENDING) {
            String state = Environment.getExternalStorageState();
            if(Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
                Log.d("", "Media avaible");
                m_refreshMediaList.execute(Environment.getExternalStorageDirectory());
            }else{
                Log.d("", "Media unavaible");
            }
        }
    }

    private void populateMediaList()
    {
        List<String> musicItems = new ArrayList<>();
        List<String> videoItems = new ArrayList<>();

        for(MediaInfo mi : m_mediaLists){
            if(mi.filename.endsWith(".mp3")) {
                if ((mi.title == null) || (mi.title != null && mi.title.isEmpty()))
                    musicItems.add(mi.filename);
                else {
                    musicItems.add(mi.title);
                }
            }else if(mi.filename.endsWith(".mp4")){
                if ((mi.title == null) || (mi.title != null && mi.title.isEmpty()))
                    videoItems.add(mi.filename);
                else {
                    videoItems.add(mi.title);
                }
            }
        }

        String[] musicArray = new String[musicItems.size()];
        musicArray = musicItems.toArray(musicArray);

        ArrayAdapter<String> adaptMusic = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, musicArray);
        m_musicListView.setAdapter(adaptMusic);

        String[] videoArray = new String[videoItems.size()];
        videoArray = videoItems.toArray(videoArray);

        ArrayAdapter<String> adaptVideo = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, videoArray);
        m_videoListView.setAdapter(adaptVideo);
    }

    private void resetUpdateAsync()
    {
        m_refreshMediaList = new AsyncTask<File, Integer, Integer>() {
            @Override
            protected Integer doInBackground(File... params) {
                List<MediaInfo> newMedia = new ArrayList<>();

                for(File root : params){
                    List<File> visited = new ArrayList<>();
                    visited.addAll(Arrays.asList(root.listFiles()));
                    Stack<File> stack = new Stack<>();
                    stack.addAll(visited);

                    while(!stack.isEmpty()){
                        File f = stack.pop();

                        if(f.isDirectory()){
                            for(File child : f.listFiles()){
                                if(!visited.contains(child)){
                                    stack.push(child);
                                    visited.add(child);
                                }
                            }
                        }else{
                            if(f.canRead() && ((f.getName().endsWith(".mp3")) || (f.getName().endsWith(".mp4")))){
                                MediaInfo mi = new MediaInfo(f.getAbsolutePath(), f.getName());
                                if(!m_mediaLists.contains(mi)){
                                    newMedia.add(mi);
                                }
                            }
                        }
                    }
                }

                for(MediaInfo mi : newMedia){
                    addToList(mi);
                }

                return newMedia.size();
            }

            @Override
            protected void onPostExecute(Integer result){
                Log.d("", result + " new files found");
                populateMediaList();
            }
        };
    }

    private void loadXML(){
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(m_mediaListXML);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Media");

            for(int i=0; i < nList.getLength(); ++i){
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    String name = element.getElementsByTagName("Name").item(0).getTextContent();
                    String path = element.getElementsByTagName("Path").item(0).getTextContent();
                    String title = element.getElementsByTagName("Title").item(0).getTextContent();

                    MediaInfo mi = new MediaInfo(path, name);
                    mi.title = title;

                    addToList(mi);
                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveXML() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Medias");
            doc.appendChild(rootElement);

            for(MediaInfo mi : m_mediaLists){
                Element media = doc.createElement("Media");

                Element name = doc.createElement("Name");
                name.appendChild(doc.createTextNode(mi.filename));
                media.appendChild(name);

                Element path = doc.createElement("Path");
                path.appendChild(doc.createTextNode(mi.path));
                media.appendChild(path);

                if(!mi.title.isEmpty()) {
                    Element title = doc.createElement("Title");
                    title.appendChild(doc.createTextNode(mi.title));
                    media.appendChild(title);
                }

                rootElement.appendChild(media);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(m_mediaListXML);

            transformer.transform(source, result);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void addToList(MediaInfo mi){
        MediaMetadataRetriever meta = new MediaMetadataRetriever();
        meta.setDataSource(mi.path);

        mi.title = meta.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        if(mi.title == null){
            mi.title = mi.filename;
        }

        if(m_mediaLists.contains(mi)){
            return;
        }

        m_mediaLists.add(mi);
        m_mediaAccessDict.put(mi.title, mi.path);
    }
}
