package com.example.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    final static int LINE = 1,CIRCLE=2,OVAL=3,RECTANGLE=4,PEN=5,PICTURE=6,ERASER=7,WHITE=8;
    static int curShape=0;
    static String setColor="black", setFilter="nofilter";
    Button btnFigure, btnTool, btnColor, btnFilter;
    ImageButton ibZoomin, ibZoomout, ibUndo, ibRedo, ibDelete;
    myGraphicView graphicView;
    static float satur=1;
    static List<Integer> shape = new ArrayList<Integer>();
    static List<Float> x = new ArrayList<Float>();
    static List<Float> y = new ArrayList<Float>();
    static List<Float> x1 = new ArrayList<Float>();
    static List<Float> y1 = new ArrayList<Float>();
    static List<String> shapecolor = new ArrayList<String>();
    static List<Bitmap> img = new ArrayList<Bitmap>();
    static List<String> drawFilter = new ArrayList<String>();
    Bitmap bitmap, captureBit, openBit;
    int i=-1,j=-1,k=-1,l=3,select=-1;
    int clearNum=0,index=0,pngCount=0,bitNum=0;
    static List<Float> tmpX = new ArrayList<Float>();
    static List<Float> tmpY = new ArrayList<Float>();
    static List<Float> tmpX1 = new ArrayList<Float>();
    static List<Float> tmpY1 = new ArrayList<Float>();
    static List<Integer> tmpShape = new ArrayList<Integer>();
    static List<String> tmpColor = new ArrayList<String>();
    static List<Bitmap> tmpBitmap = new ArrayList<Bitmap>();
    static List<String> tmpdrawFilter = new ArrayList<String>();
    File file;
    String[] imgList;

    //메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0,1,0,"불러오기");
        menu.add(0,2,0,"저장");
        return true;
    }
    //메뉴선택 했을 때
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()){
            case 1: //불러오기
                imgList = new String[pngCount];
                for(int i=0; i<imgList.length; i++){
                    imgList[i]="/sdcard/GraphicEditor/projectImage"+i+".png";
                }

                AlertDialog.Builder open = new AlertDialog.Builder(MainActivity.this);
                open.setTitle("이미지 목록");
                open.setIcon(R.mipmap.ic_launcher);
                open.setSingleChoiceItems(imgList, select, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        select = which;
                    }
                }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        curShape=WHITE;
                        openBit = BitmapFactory.decodeFile(imgList[select]);

                        bitNum=1;

                        shape.clear();
                        x.clear();
                        y.clear();
                        x1.clear();
                        y1.clear();
                        shapecolor.clear();
                        img.clear();
                        drawFilter.clear();

                        btnFigure.setText("도형");
                        btnTool.setText("도구");
                        btnColor.setText("색깔");
                        btnFilter.setText("필터없음");

                        graphicView.openBit=openBit;
                        graphicView.invalidate();
                    }
                });
                open.show();
                break;
            case 2: //저장
                graphicView.invalidate();
                graphicView.buildDrawingCache();
                captureBit = graphicView.getDrawingCache();

                file = new File("/sdcard/GraphicEditor/projectImage"+pngCount+".png");
                pngCount++;

                OutputStream out = null;
                try{
                    out = new FileOutputStream(file);
                    if(out!=null){
                        captureBit.compress(Bitmap.CompressFormat.PNG, 75, out);
                        Toast.makeText(MainActivity.this,"저장 완료",Toast.LENGTH_SHORT).show();
                    }
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this,"bbbb",Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(new myGraphicView(this));
        setContentView(R.layout.activity_main);
        setTitle("그래픽 에디터");

        btnFigure = (Button)findViewById(R.id.btnFigure);
        btnTool = (Button)findViewById(R.id.btnTool);
        btnColor = (Button)findViewById(R.id.btnColor);
        btnFilter = (Button)findViewById(R.id.btnFilter);

        //도형 버튼
        btnFigure.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String[] arrItem1 = new String[] {"선","원","타원","사각형"};
                AlertDialog.Builder mydlg1 = new AlertDialog.Builder(MainActivity.this);
                mydlg1.setTitle("도형");
                mydlg1.setSingleChoiceItems(arrItem1, i, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            curShape=LINE;
                            i=0;
                        }else if(which==1){
                            curShape=CIRCLE;
                            i=1;
                        }else if(which==2){
                            curShape=OVAL;
                            i=2;
                        }else if(which==3){
                            curShape=RECTANGLE;
                            i=3;
                        }
                        btnFigure.setText(arrItem1[which]);
                        btnTool.setText("도구");
                        j=0;
                        Toast.makeText(MainActivity.this,arrItem1[which]+" 선택",Toast.LENGTH_SHORT).show();
                    }
                });
                mydlg1.setIcon(R.mipmap.ic_launcher);
                mydlg1.setPositiveButton("확인",null);
                mydlg1.show();
            }
        });
        //도구 버튼
        btnTool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] arrItem2 = new String[] {"펜","그림","지우개"};
                final AlertDialog.Builder mydlg2 = new AlertDialog.Builder(MainActivity.this);
                mydlg2.setTitle("도구");
                mydlg2.setSingleChoiceItems(arrItem2, j, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            curShape=PEN;
                            j=0;
                        }else if(which==1){//비트맵 불러오기
                            curShape=PICTURE;
                            j=1;
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(intent, 1);
                        }else if(which==2){
                            curShape=ERASER;
                            j=2;
                        }
                        btnTool.setText(arrItem2[which]);
                        btnFigure.setText("도형");
                        i=0;
                        Toast.makeText(MainActivity.this,arrItem2[which]+" 선택",Toast.LENGTH_SHORT).show();
                    }
                });
                mydlg2.setIcon(R.mipmap.ic_launcher);
                mydlg2.setPositiveButton("확인",null);
                mydlg2.show();
            }
        });
        //색깔버튼
        btnColor.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String[] arrItem3 = new String[] {"검은색","빨간색","초록색","파란색"};
                AlertDialog.Builder mydlg3 = new AlertDialog.Builder(MainActivity.this);
                mydlg3.setTitle("색깔");
                mydlg3.setSingleChoiceItems(arrItem3, k, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            setColor="black";
                            k=0;
                        }else if(which==1){
                            setColor="red";
                            k=1;
                        }else if(which==2){
                            setColor="green";
                            k=2;
                        }else{
                            setColor="blue";
                            k=3;
                        }
                        btnColor.setText(arrItem3[which]);
                        Toast.makeText(MainActivity.this,arrItem3[which]+" 선택",Toast.LENGTH_SHORT).show();
                    }
                });
                mydlg3.setIcon(R.mipmap.ic_launcher);
                mydlg3.setPositiveButton("확인",null);
                mydlg3.show();
            }
        });
        //필터 버튼
        btnFilter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String[] arrItem4 = new String[]{"흑백","블러링","엠보싱","필터없음"};
                AlertDialog.Builder mydlg4 = new AlertDialog.Builder(MainActivity.this);
                mydlg4.setTitle("필터");
                mydlg4.setSingleChoiceItems(arrItem4, l, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            setFilter="gray";
                            satur=0;
                            l=0;
                        }else if(which==1){
                            setFilter="blurring";
                            satur=1;
                            l=1;
                        }else if(which==2){
                            setFilter="embossing";
                            satur=1;
                            l=2;
                        }else if(which==3){
                            setFilter="nofilter";
                            satur=1;
                            l=3;
                        }
                        btnFilter.setText(arrItem4[which]);
                        Toast.makeText(MainActivity.this, arrItem4[which] + " 선택", Toast.LENGTH_SHORT).show();
                    }
                });
                mydlg4.setIcon(R.mipmap.ic_launcher);
                mydlg4.setPositiveButton("확인", null);
                mydlg4.show();
            }
        });



        LinearLayout pictureLayout = (LinearLayout)findViewById(R.id.pictureLayout);
        graphicView = (myGraphicView) new myGraphicView(this);
        pictureLayout.addView(graphicView);

        clickIcons();
        graphicView.invalidate();
    }

    //비트맵 불러오기
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try {
                    //핸드폰에 있는 이미지 불러오기
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    bitmap = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //이미지 아이콘
    private void clickIcons() {
        //확대
        ibZoomin = (ImageButton) findViewById(R.id.ibZoomin);
        ibZoomin.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                if(shape.size()!=0) {
                    if ((Math.sqrt(Math.pow(x.get(x.size() - 1) - x1.get(x.size() - 1), 2)) < 1500)
                            && (Math.sqrt(Math.pow(y.get(y.size() - 1) - y1.get(y.size() - 1), 2))) < 2500) {
                        if (curShape == LINE || curShape == OVAL || curShape == RECTANGLE) {
                            float numX = x.get(x.size() - 1);
                            float numY = y.get(y.size() - 1);
                            float numX1 = x1.get(x1.size() - 1);
                            float numY1 = y1.get(y1.size() - 1);

                            if ((x.get(x.size() - 1) < x1.get(x1.size() - 1))
                                    && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                float numXX = (x1.get(x1.size() - 1) - x.get(x.size() - 1)) * (float) 0.1;
                                float numYY = (y1.get(y1.size() - 1) - y.get(y.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX - numXX);
                                y.add(numY - numYY);
                                x1.add(numX1 + numXX);
                                y1.add(numY1 + numYY);
                            } else if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                float numXX = (x1.get(x1.size() - 1) - x.get(x.size() - 1)) * (float) 0.1;
                                float numYY = (y.get(y.size() - 1) - y1.get(y1.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX - numXX);
                                y.add(numY + numYY);
                                x1.add(numX1 + numXX);
                                y1.add(numY1 - numYY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                float numXX = (x.get(x.size() - 1) - x1.get(x1.size() - 1)) * (float) 0.1;
                                float numYY = (y.get(y.size() - 1) - y1.get(y1.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX + numXX);
                                y.add(numY + numYY);
                                x1.add(numX1 - numXX);
                                y1.add(numY1 - numYY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                float numXX = (x.get(x.size() - 1) - x1.get(x1.size() - 1)) * (float) 0.1;
                                float numYY = (y1.get(y1.size() - 1) - y.get(y.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX + numXX);
                                y.add(numY - numYY);
                                x1.add(numX1 - numXX);
                                y1.add(numY1 + numYY);
                            }
                        } else if (curShape == CIRCLE) {
                            float numX1 = x1.get(x1.size() - 1);
                            float numY1 = y1.get(y1.size() - 1);
                            float radiusX = x1.get(x1.size() - 1) * (float) 0.05;
                            float radiusY = y1.get(y1.size() - 1) * (float) 0.05;

                            if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 + radiusX);
                                y1.add(numY1 + radiusY);
                            } else if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 + radiusX);
                                y1.add(numY1 - radiusY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 - radiusX);
                                y1.add(numY1 - radiusY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 - radiusX);
                                y1.add(numY1 + radiusY);
                            }
                        }
                    }
                }
                graphicView.invalidate();
            }
        });
        //축소
        ibZoomout = (ImageButton) findViewById(R.id.ibZoomout);
        ibZoomout.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                if(shape.size()!=0) {
                    if (Math.sqrt(Math.pow(x.get(x.size() - 1) - x1.get(x1.size() - 1), 2)) > 10) {
                        if (curShape == LINE || curShape == OVAL || curShape == RECTANGLE) {
                            float numX = x.get(x.size() - 1);
                            float numY = y.get(y.size() - 1);
                            float numX1 = x1.get(x1.size() - 1);
                            float numY1 = y1.get(y1.size() - 1);

                            if ((x.get(x.size() - 1) < x1.get(x1.size() - 1))
                                    && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                float numXX = (x1.get(x1.size() - 1) - x.get(x.size() - 1)) * (float) 0.1;
                                float numYY = (y1.get(y1.size() - 1) - y.get(y.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX + numXX);
                                y.add(numY + numYY);
                                x1.add(numX1 - numXX);
                                y1.add(numY1 - numYY);
                            } else if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                float numXX = (x1.get(x1.size() - 1) - x.get(x.size() - 1)) * (float) 0.1;
                                float numYY = (y.get(y.size() - 1) - y1.get(y1.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX + numXX);
                                y.add(numY - numYY);
                                x1.add(numX1 - numXX);
                                y1.add(numY1 + numYY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                float numXX = (x.get(x.size() - 1) - x1.get(x1.size() - 1)) * (float) 0.1;
                                float numYY = (y.get(y.size() - 1) - y1.get(y1.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX - numXX);
                                y.add(numY - numYY);
                                x1.add(numX1 + numXX);
                                y1.add(numY1 + numYY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                float numXX = (x.get(x.size() - 1) - x1.get(x1.size() - 1)) * (float) 0.1;
                                float numYY = (y1.get(y1.size() - 1) - y.get(y.size() - 1)) * (float) 0.1;

                                x.remove(x.size() - 1);
                                y.remove(y.size() - 1);
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x.add(numX - numXX);
                                y.add(numY + numYY);
                                x1.add(numX1 + numXX);
                                y1.add(numY1 - numYY);
                            }
                        } else if (curShape == CIRCLE) {
                            float numX1 = x1.get(x1.size() - 1);
                            float numY1 = y1.get(y1.size() - 1);
                            float radiusX = x1.get(x1.size() - 1) * (float) 0.05;
                            float radiusY = y1.get(y1.size() - 1) * (float) 0.05;


                            if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 - radiusX);
                                y1.add(numY1 - radiusY);
                            } else if ((x.get(x.size() - 1) < x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 - radiusX);
                                y1.add(numY1 + radiusY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) > y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 + radiusX);
                                y1.add(numY1 + radiusY);
                            } else if ((x.get(x.size() - 1) > x1.get(x1.size() - 1)) && (y.get(y.size() - 1) < y1.get(y1.size() - 1))) {
                                x1.remove(x1.size() - 1);
                                y1.remove(y1.size() - 1);

                                x1.add(numX1 + radiusX);
                                y1.add(numY1 - radiusY);
                            }
                        }
                    }
                }
                graphicView.invalidate();
            }
        });
        //뒤로가기
        ibUndo = (ImageButton) findViewById(R.id.ibUndo);
        ibUndo.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                if(shape.size()!=0) {
                    tmpShape.add(shape.get(shape.size() - 1));
                    tmpX.add(x.get(x.size() - 1));
                    tmpY.add(y.get(y.size() - 1));
                    tmpX1.add(x1.get(x1.size() - 1));
                    tmpY1.add(y1.get(y1.size() - 1));
                    tmpColor.add(shapecolor.get(shapecolor.size() - 1));
                    tmpBitmap.add(img.get(img.size() - 1));
                    tmpdrawFilter.add(drawFilter.get(drawFilter.size()-1));
                    //index++;

                    shape.remove(shape.size() - 1);
                    x.remove(x.size() - 1);
                    y.remove(y.size() - 1);
                    x1.remove(x1.size() - 1);
                    y1.remove(y1.size() - 1);
                    shapecolor.remove(shapecolor.size() - 1);
                    img.remove(img.size() - 1);
                    drawFilter.remove(drawFilter.size()-1);
                }
                graphicView.invalidate();
            }
        });
        //앞으로가기
        ibRedo = (ImageButton) findViewById(R.id.ibRedo);
        ibRedo.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                if(index!=tmpShape.size()) {
                    shape.add(tmpShape.get(tmpShape.size()-index-1));
                    x.add(tmpX.get(tmpShape.size()-index-1));
                    y.add(tmpY.get(tmpShape.size()-index-1));
                    x1.add(tmpX1.get(tmpShape.size()-index-1));
                    y1.add(tmpY1.get(tmpShape.size()-index-1));
                    shapecolor.add(tmpColor.get(tmpShape.size()-index-1));
                    img.add(tmpBitmap.get(tmpShape.size()-index-1));
                    drawFilter.add(tmpdrawFilter.get(tmpdrawFilter.size()-index-1));
                    index++;
                }
                graphicView.invalidate();
            }
        });
        //초기화
        ibDelete = (ImageButton) findViewById(R.id.ibDelete);
        ibDelete.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v) {
                clearNum=1;
                i=-1;
                graphicView.invalidate();
            }
        });
    }


    public class myGraphicView extends View {
        public Bitmap openBit;
        float startX=-1,startY=-1,stopX=-1,stopY=-1,preX=-1,preY=-1,moveX=-1,moveY=-1;

        public myGraphicView(Context context) {
            super(context);
        }
        //마우스 클릭 이벤트 처리
        public boolean onTouchEvent(MotionEvent event){
            super.onTouchEvent(event);

            if(clearNum==1){    //초기화
                shape.clear();
                x.clear();
                y.clear();
                x1.clear();
                y1.clear();
                shapecolor.clear();
                img.clear();
                drawFilter.clear();

                tmpShape.clear();
                tmpX.clear();
                tmpY.clear();
                tmpX1.clear();
                tmpY1.clear();
                tmpColor.clear();
                tmpBitmap.clear();
                tmpdrawFilter.clear();

                this.invalidate();
                clearNum=0;
                index=0;
                curShape=WHITE;
                setColor="black";
                k=0;
                btnFigure.setText("도형");
                btnTool.setText("도구");
                btnColor.setText("색깔");
                btnFilter.setText("필터없음");
            }
            if(curShape==PEN) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        preX=startX;
                        preY=startY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopX = event.getX();
                        stopY = event.getY();

                        shape.add(curShape);
                        x.add(preX);
                        y.add(preY);
                        x1.add(stopX);
                        y1.add(stopY);
                        shapecolor.add(setColor);
                        img.add(null);
                        if(setFilter.equals("gray")){
                            drawFilter.add("gray");
                        }else if(setFilter.equals("blurring")){
                            drawFilter.add("blurring");
                        }else if(setFilter.equals("embossing")){
                            drawFilter.add("embossing");
                        }else{
                            drawFilter.add("null");
                        }

                        this.invalidate();
                        preX=stopX;
                        preY=stopY;
                        break;
                    case MotionEvent.ACTION_UP:
                        stopX = event.getX();
                        stopY = event.getY();

                        this.invalidate();
                        preX=-1;
                        preY=-1;
                        break;
                }
            }else if(curShape==PICTURE){
                if(event.getAction()==MotionEvent.ACTION_DOWN){
                    startX = event.getX();
                    startY = event.getY();
                }else if(event.getAction()==MotionEvent.ACTION_MOVE){
                    stopX = event.getX();
                    stopY = event.getY();
                    moveX = startX - stopX;
                    moveY = startY - stopY;

                    this.invalidate();
                    startX = stopX;
                    startY = stopY;
                }else if(event.getAction()==MotionEvent.ACTION_UP){
                    stopX = event.getX();
                    stopY = event.getY();

                    shape.add(curShape);
                    x.add(startX);
                    y.add(startY);
                    x1.add(moveX);
                    y1.add(moveY);
                    shapecolor.add("black");
                    img.add(bitmap);
                    if(setFilter.equals("gray")){
                        drawFilter.add("gray");
                    }else if(setFilter.equals("blurring")){
                        drawFilter.add("blurring");
                    }else if(setFilter.equals("embossing")){
                        drawFilter.add("embossing");
                    }else{
                        drawFilter.add("null");
                    }

                    this.invalidate();
                }
            }else if(curShape==ERASER){
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        preX=startX;
                        preY=startY;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        stopX = event.getX();
                        stopY = event.getY();

                        shape.add(curShape);
                        x.add(preX);
                        y.add(preY);
                        x1.add(stopX);
                        y1.add(stopY);
                        shapecolor.add("white");
                        img.add(null);
                        if(setFilter.equals("gray")){
                            drawFilter.add("gray");
                        }else if(setFilter.equals("blurring")){
                            drawFilter.add("blurring");
                        }else if(setFilter.equals("embossing")){
                            drawFilter.add("embossing");
                        }else{
                            drawFilter.add("null");
                        }

                        this.invalidate();
                        preX=stopX;
                        preY=stopY;
                        break;
                    case MotionEvent.ACTION_UP:
                        stopX = event.getX();
                        stopY = event.getY();

                        this.invalidate();
                        preX=-1;
                        preY=-1;
                        break;
                }
            }else if(curShape!=PEN) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startX = event.getX();
                    startY = event.getY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    stopX = event.getX();
                    stopY = event.getY();
                    this.invalidate();
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopX = event.getX();
                    stopY = event.getY();

                    shape.add(curShape);
                    x.add(startX);
                    y.add(startY);
                    x1.add(stopX);
                    y1.add(stopY);
                    shapecolor.add(setColor);
                    img.add(null);
                    if(setFilter.equals("gray")){
                        drawFilter.add("gray");
                    }else if(setFilter.equals("blurring")){
                        drawFilter.add("blurring");
                    }else if(setFilter.equals("embossing")){
                        drawFilter.add("embossing");
                    }else{
                        drawFilter.add("null");
                    }

                    this.invalidate();
                }
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            ColorMatrix cm = new ColorMatrix();


            Paint paint = new Paint();
            paint.setAntiAlias(true);

            switch (setColor) {
                case "black":
                    paint.setColor(Color.BLACK);
                    break;
                case "red":
                    paint.setColor(Color.RED);
                    break;
                case "green":
                    paint.setColor(Color.GREEN);
                    break;
                default:
                    paint.setColor(Color.BLUE);
                    break;
            }

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);

            //마우스 누르고 있을 때 그리기
            switch (curShape) {
                case LINE:
                    canvas.drawLine(startX, startY, stopX, stopY, paint);
                    break;
                case CIRCLE:
                    float radius = (float) Math.sqrt(Math.pow(stopX - startX, 2)
                            + Math.pow(stopY - startY, 2));
                    canvas.drawCircle(startX, startY, radius, paint);
                    break;
                case OVAL:
                    float radiusX = (float) Math.sqrt(Math.pow(stopX - startX, 2));
                    float radiusY = (float) Math.sqrt(Math.pow(stopY - startY, 2));
                    canvas.drawRoundRect(startX, startY, stopX, stopY, radiusX, radiusY, paint);
                    break;
                case RECTANGLE:
                    canvas.drawRect(startX, startY, stopX, stopY, paint);
                    break;
            }
            //그리기
            for (int i = 0; i < shape.size(); i++) {
                if (drawFilter.get(i).equals("gray")) {
                    cm.setSaturation(satur);
                    paint.setColorFilter(new ColorMatrixColorFilter(cm));
                } else if (drawFilter.get(i).equals("blurring")) {
                    paint.setMaskFilter(new BlurMaskFilter(15, BlurMaskFilter.Blur.NORMAL));
                } else if (drawFilter.get(i).equals("embossing")) {
                    paint.setMaskFilter(new EmbossMaskFilter(new float[]{3, 3, 10}, 0.9f, 12, 30));
                } else if (drawFilter.get(i).equals("null")) {
                    paint.setMaskFilter(null);
                }

                if (shapecolor.get(i).equals("black")) {
                    paint.setColor(Color.BLACK);
                } else if (shapecolor.get(i).equals("red")) {
                    paint.setColor(Color.RED);
                } else if (shapecolor.get(i).equals("green")) {
                    paint.setColor(Color.GREEN);
                } else if (shapecolor.get(i).equals("blue")) {
                    paint.setColor(Color.BLUE);
                } else if (shapecolor.get(i).equals("white")) {
                    paint.setColor(Color.WHITE);
                }

                switch (shape.get(i)) {
                    case 1:
                        canvas.drawLine(x.get(i), y.get(i), x1.get(i), y1.get(i), paint);
                        break;
                    case 2:
                        canvas.drawCircle(x.get(i), y.get(i), (float) Math.sqrt(Math.pow(x1.get(i) - x.get(i), 2)
                                + Math.pow(y1.get(i) - y.get(i), 2)), paint);
                        break;
                    case 3:
                        canvas.drawRoundRect(x.get(i), y.get(i), x1.get(i), y1.get(i),
                                (float) Math.sqrt(Math.pow(x1.get(i) - x.get(i), 2)), (float) Math.sqrt(Math.pow(y1.get(i) - y.get(i), 2)), paint);
                        break;
                    case 4:
                        canvas.drawRect(x.get(i), y.get(i), x1.get(i), y1.get(i), paint);
                        break;
                    case 5:
                        canvas.drawLine(x.get(i), y.get(i), x1.get(i), y1.get(i), paint);
                        break;
                    case 6:
                        canvas.drawBitmap(img.get(i), x.get(i) - x1.get(i), y.get(i) - y1.get(i), paint);
                        break;
                    case 7:
                        canvas.drawLine(x.get(i), y.get(i), x1.get(i), y1.get(i), paint);
                        break;
                }
            }


            if(openBit!=null){
                bitNum=0;
                canvas.drawBitmap(openBit,0,0,null);
            }
        }
    }
}