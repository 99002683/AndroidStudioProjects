package com.example.texttospeechreadout;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
{
    private static EditText textView;
    TextToSpeech tts;

    private static final int OPEN_REQUEST_CODE = 41;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (EditText) findViewById(R.id.fileText);

        Button b1 =(Button)findViewById(R.id.button1);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData)
    {

        super.onActivityResult(requestCode, resultCode, resultData);
        Uri currentUri = null;

        if (resultCode == Activity.RESULT_OK)
        {
            if (requestCode == OPEN_REQUEST_CODE)
            {
            if (resultData != null)
            {
                currentUri = resultData.getData();
                try
                {

                    String content = readFileContent(currentUri);
                    textView.setText(content);
                } catch (IOException e)
                {
                    Log.e(getString(R.string.error_tag), "I got an error", e);
                }
                }
            }
        }

    }




    public void openFile(View view)
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, OPEN_REQUEST_CODE);
    }

    private String readFileContent(Uri uri) throws IOException
    {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String currentline;
        while ((currentline = reader.readLine()) != null)
        {
            stringBuilder.append(currentline + "\n");
        }
        inputStream.close();
        return stringBuilder.toString();
    }

    public void speak()
    {
        if(textView.getText().toString().trim().length()==0)
        {
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Error");
            builder.setMessage("Nothing to speak. Please type or record some text.");
            AlertDialog dialog=builder.create();
            dialog.show();
        }
        else
        {
            tts = new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener()
            {
                public void onInit(int status)
                {
                    if(status!=TextToSpeech.ERROR)
                    {
                        tts.setLanguage(Locale.US);
                        String str=textView.getText().toString();
                        tts.speak(str,TextToSpeech.QUEUE_ADD,null);
                        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public void onPause()
    {
        if(tts!=null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

}
