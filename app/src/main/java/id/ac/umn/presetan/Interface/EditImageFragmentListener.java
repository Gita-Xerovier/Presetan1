package id.ac.umn.presetan.Interface;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onConstraintChanged(float constraint);
    void onEditStarted();
    void onEditCompleted();
}
