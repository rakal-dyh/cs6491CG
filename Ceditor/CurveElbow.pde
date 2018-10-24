class CurveElbow{
  Elbow[] elbows;
  float[] twist_end_angles;
  
  CurveElbow(Elbow[] elbows){
    this.elbows=elbows;
    twist_end_angles = new float[elbows.length];
  }


  void draw(){
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
