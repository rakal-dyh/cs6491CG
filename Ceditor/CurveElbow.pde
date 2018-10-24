import java.lang.Float;

class CurveElbow{
  Elbow[] elbows;
  float[] twist_end_angles;
  float[] diffK;

  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    twist_end_angles = new float[elbows.length];
    calculateDiffK();
    twistForDiffK();
    calculate_twist_end_angles();
  }

  void calculateDiffK(){
    int n=elbows.length;
    diffK=new float[n];
    diffK[0]=0.0;
    for(int i=1;i<n;i++){
      //diffK[i] += angle(elbows[i-1].KS,elbows[i].KS);
      diffK[i] += calculate_tail_head_angle(elbows[i - 1], elbows[i]);
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
    float total_twist_end_angles = calculate_tail_head_angle(elbows[elbows.length - 1], elbows[0]);
    float total_length = 0;
    for (Elbow e : elbows) total_length += e.length;
    for (int i = 0; i < elbows.length; i++) {
      twist_end_angles[i] = total_twist_end_angles * elbows[i].length / total_length;
    }
  }
  
  float calculate_tail_head_angle(Elbow ea, Elbow eb) {
    vec ea_vec = V(ea.circle_vectors[ea.num_of_circles][0]);
    vec eb_vec = V(eb.circle_vectors[0][0]);
    return angle(ea_vec, eb_vec);
  }
}
