import java.lang.Float;

class CurveElbow{
  Elbow[] elbows;
<<<<<<< HEAD
  float[] twist_end_angles;
  
  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    twist_end_angles = new float[elbows.length];
=======
  float[] diffK;

  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    calculateDiffK();
    twistForDiffK();
>>>>>>> e219cd026cd33287c2e66fed9f144d8791fe7a7d
  }

  void calculateDiffK(){
    int n=elbows.length;
    diffK=new float[n];
    diffK[0]=0.0;
    for(int i=1;i<n;i++){
      diffK[i]=angle(elbows[i-1].KS,elbows[i].KS);
      if (i==1){
        System.out.println(elbows[i-1].KS.x);
        System.out.println(elbows[i-1].KS.y);
        System.out.println(elbows[i-1].KS.z);
        System.out.println(elbows[i].KS.x);
        System.out.println(elbows[i].KS.y);
        System.out.println(elbows[i].KS.z);
        System.out.println(diffK[i]);
        System.out.println('-');
      }

      //System.out.println(diffK[i]);
      if(diffK[i]!=diffK[i]) diffK[i]=0;

    }
  }

  void twistForDiffK(){
    int n=elbows.length;
    for(int i=0;i<n;i++){
      //System.out.println(diffK[i]);
      elbows[i].twist_all(diffK[i]);
    }
  }



  void draw(){
    for(int i=0;i<elbows.length;i++){
      drawElbow(elbows[i]);
      if (i==0) drawTorusAroundStartCircle(elbows[i]);
    }
  }
  
  void calculate_twist_end_angles() {
    Elbow first = elbows[0];
    Elbow last = elbows[elbows.length - 1];
    vec start_center_vec = V(first.circle_vectors[0][0]);
    vec end_center_vec = V(last.circle_vectors[last.num_of_circles][0]);
    float total_twist_end_angles = angle(start_center_vec, end_center_vec);
    float total_length = 0;
    for (Elbow e : elbows) total_length += e.length;
    for (int i = 0; i < elbows.length; i++) {
      twist_end_angles[i] = total_twist_end_angles * elbows[i].length / total_length;
    }
  }
}
