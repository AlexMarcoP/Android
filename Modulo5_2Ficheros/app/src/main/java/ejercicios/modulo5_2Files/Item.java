package ejercicios.modulo5_2Files;

public class Item {

        private int image;
        private String title;
        private String url;

        Item() {
            super();
        }

        Item(int image, String title, String url) {
            super();
            this.image = image;
            this.title = title;
            this.url = url;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

