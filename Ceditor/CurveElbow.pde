import java.lang.Float;

class CurveElbow{
  Elbow[] elbows;
  float[] twist_end_angles;
  //float[] diffK;

  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    twist_end_angles = new float[elbows.length];
    //diffK = new float[elbows.length];
    calculate_and_twist_all_diffK();
    calculate_twist_end_angles();
  }

  void calculate_and_twist_all_diffK(){
    //diffK[0] = 0;
    for(int i=1;i<elbows.length;i++){

      vec vec1 = elbows[i - 1].circle_vectors[elbows[i - 1].num_of_circles][0];
      if (elbows[i - 1].isTwisted) vec1 = V(elbows[i - 1].circle_vectors_twisted[elbows[i - 1].num_of_circles][0]);
      vec old_vec2 = elbows[i].circle_vectors[0][0];
      if (elbows[i].isTwisted) old_vec2 = V(elbows[i].circle_vectors_twisted[0][0]);
      
      float curr_diffK = calculate_tail_head_angle(elbows[i - 1], elbows[i]);
      if(curr_diffK!=curr_diffK) curr_diffK=0;
      
      elbows[i].twist_all(curr_diffK);
      vec vec2 = elbows[i].circle_vectors_twisted[0][0];
      
      if (norm(M(vec1, vec2))>0.001) elbows[i].twist_all(-2 * curr_diffK);
      
      if (true) { //norm(M(vec1, vec2))>0.01) {
        System.out.println(vec1.x + " " + vec1.y + " " + vec1.z);
        System.out.println(old_vec2.x + " " + old_vec2.y + " " + old_vec2.z);
        System.out.println(curr_diffK);
        System.out.println(vec1.x + " " + vec1.y + " " + vec1.z);
        System.out.println(vec2.x + " " + vec2.y + " " + vec2.z);
        float new_diffK = angle(old_vec2, vec2);
        System.out.println(new_diffK);
        System.out.println(angle(vec1, vec2));
        /*
        vec1n = V(vec1).normalize();
        old_vec2n = V(old_vec2).normalize();
        vec1nnormal = B(vec1n, old_vec2n);
        vec1nnormal.normalize();
        twisted = R(old_vec2, curr_diffK, vec1n, vec1nnormal);
        System.out.println(twisted.x + " " + twisted.y + " " + twisted.z);
        System.out.println(angle(vec1, twisted));
        */
        System.out.println("-------");
      }
      
      /*
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
      */
      
      
      //if(diffK[i]!=diffK[i]) diffK[i]=0;
      
    }
    
    /*
    for(int i=1;i<elbows.length;i++){
      //System.out.println(diffK[i]);
      elbows[i].twist_all(diffK[i]);
    }
    */
  }

  /*
  void twistForDiffK(){
    int n=elbows.length;
    for(int i=0;i<n;i++){
      //System.out.println(diffK[i]);
      elbows[i].twist_all(diffK[i]);
    }
  }
  */

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
    vec ea_vec = ea.circle_vectors[ea.num_of_circles][0];
    if (ea.isTwisted) ea_vec = ea.circle_vectors_twisted[ea.num_of_circles][0];
    vec eb_vec = eb.circle_vectors[0][0];
    if (eb.isTwisted) eb_vec = eb.circle_vectors_twisted[0][0];
    return angle(ea_vec, eb_vec);
  }
}
