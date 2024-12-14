package com.trademarket.tzm.user.model;

public class Preferences {

    private Visibility visibility = Visibility.PUBLIC;
    private boolean showBio;
    private Layout layout = Layout.DETAILED;

    public Preferences() {}

    public Preferences(Visibility visibility, boolean showBio, Layout layout) {
        this.visibility = visibility;
        this.showBio = showBio;
        this.layout = layout;
    }

    public Visibility getVisibility() { return visibility; }
    public void setVisibility(Visibility visibility) { this.visibility = visibility; }

    public boolean isShowBio() { return showBio; }
    public void setShowBio(boolean showBio) { this.showBio = showBio; }

    public Layout getLayout() { return layout; }
    public void setLayout(Layout layout) { this.layout = layout; }

    @Override
    public String toString() {
        return "Preferences{" +
                "visibility=" + visibility +
                ", showBio=" + showBio +
                ", layout=" + layout +
                '}';
    }

    public enum Visibility {
        PRIVATE("Private - Only you can see this"),
        PUBLIC("Public - Everyone can see this");
    
        private final String description;
    
        Visibility(String description) {
            this.description = description;
        }
    
        public String getDescription() { return description; }
    }    

    public enum Layout {
        DETAILED, COMPACT
    }
}

