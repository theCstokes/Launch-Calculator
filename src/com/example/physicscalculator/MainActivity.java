package com.example.physicscalculator;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	//Variables 
	static boolean xReached = false;
    static boolean groundReached = false;
    static double netX, netY;
    static double v;
    static double vx = 1, vy = 1;
    static double v1x = 1, v2x = 1;
    static double v1y = 1, v2y = 1;
    static double ax, ay;
    static double x1 = 0, x2;
    static double y1 = 0, y2;
    static double d, dx, dy;
    static double t = 0;
    static double m;
    static double g = -9.8;
    static double a = (Math.PI * Math.pow(0.0038, 2));
    static double p = 1.225;
    static double Cd = 0.47;
    static double k;
    static double con = 161.52;
    static double E = 0.27;
    static double fin;
	
	Button startButton, backButton;
	EditText disText;
	EditText massText;
	CheckBox airResistanceCheck, airPressureCheck;

	String dis, mass; 
	double targetDis = 0;
	double extent;
	
	boolean outPage = false;
	boolean airResistanceFlag = false;
	boolean airPressureFlag = false;

	//Interesting but not relevant to physics android code
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		startButton = (Button) findViewById(R.id.btn_run);
		backButton = (Button) findViewById(R.id.btn_back);
		disText = (EditText) findViewById(R.id.txt_dis);
		massText = (EditText) findViewById(R.id.txt_mass);
		airResistanceCheck = (CheckBox) findViewById(R.id.check_air);
		airPressureCheck = (CheckBox) findViewById(R.id.check_pressure);

		airResistanceCheck.setChecked(true);
		airPressureCheck.setChecked(false);

		
		startButton.setOnClickListener(new View.OnClickListener()
	    {
			@Override
			public void onClick(View v) {
				dis = disText.getText().toString();
				mass = massText.getText().toString();			
				if (mass.matches("[0-9]+")){ 
					m = Double.valueOf(mass);
					m = m / 1000;
				}
				init();
				if (dis.matches("[0-9]+")){ 
					targetDis = Double.valueOf(dis);
					double outVal = extentionDis(targetDis);
					String eout = String.valueOf(outVal);
					char[] Eout = eout.toCharArray();
					CharSequence cs = new String(Eout);
					setContentView(R.layout.activity_display);
					outPage = true;
					TextView eOut = (TextView) findViewById(R.id.ExtOut);
					
					
					String v1out = String.valueOf(fin);
					char[] Vout = v1out.toCharArray();
					CharSequence cs2 = new String(Vout);
					outPage = true;
					TextView vOut = (TextView) findViewById(R.id.VolOut);
					eOut.setText(cs);
					vOut.setText(cs2);
				}
				else{
					//bad user
				}
			}
		
	    });
		
		if(outPage){
			backButton.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v) {
					//outPage = false;
					System.out.println("back");
					setContentView(R.layout.activity_main);
					
				}
				
			});			
		}
		

		if(airResistanceCheck.isChecked()){
			airResistanceCheck.setText("Active");
		}
		else if(!airResistanceCheck.isChecked()){
			airResistanceCheck.setText("Inactive");
		}
		 
		airResistanceCheck.setOnClickListener(new View.OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {			  
			  if (((CheckBox) v).isChecked()) {
				  airResistanceFlag = true;
			  }	 
		  }
		});
		

		if(airPressureCheck.isChecked()){
			airResistanceCheck.setText("Active");
		}
		else if(!airPressureCheck.isChecked()){
			airPressureCheck.setText("Inactive");
		}
		 
		airPressureCheck.setOnClickListener(new View.OnClickListener() {
	 
		  @Override
		  public void onClick(View v) {
			  if (((CheckBox) v).isChecked()) {
				  airResistanceFlag = true;
			  }	 
		  }
		});

		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	//Interesting and relevant to physics calculation code
	
	public static void init(){
        k = 0.5 * p * Cd * a;
    }
    
    public static double extentionDis(double targetDis){
        while (!xReached){
                while (!groundReached){
                    v1x = v2x;
                    v1y = v2y;
                    t += 0.00001;
                    dx = k * Math.pow(v1x, 2);
                    dy = k * Math.pow(v1y, 2);
                    netX = -dx;
                    netY = (m * g) + -dy;				
                    ax = netX/m;
                    ay = netY/m;
                    v2x = v1x + (ax * t);
                    v2y = v1y + (ay * t);
                    x2 = x1 + (v1x * t) + (0.5*ax*Math.pow(t, 2));
                    y2 = y1 + (v1y * t) + (0.5*ay*Math.pow(t, 2));
                    x1 = x2;
                    y1 = y2;
                    if(y2 <= 0){
                            groundReached = true;
                    }
                }

                if(x2 <= (targetDis + 0.5) && x2 >= (targetDis - 0.5)){
                        xReached = true;
                        v = Math.sqrt((Math.pow(v2x, 2) + Math.pow(v2y, 2)));
                }
                else {
                        t = 0;
                        vx += 0.1;
                        vy += 0.1;
                        v2x = vx;
                        v2y = vy;
                        x1 = 0;
                        y1 = 0;
                        groundReached = false;
                }
        }
        fin = v;
        return getForce(v);
}
    
    public static double getForce(double Vol){
    	return Math.sqrt((-1*m*g*(1/Math.sqrt(2))) + Math.sqrt((Math.pow((m*g*(1/Math.sqrt(2))), 2) + 2*con*E*m*Math.pow(Vol, 2)))/(con*E));
    }

}
