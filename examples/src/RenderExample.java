import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bigwoah.flumpgdx.display.FlumpAnimation;
import com.bigwoah.flumpgdx.library.FlumpLibraryFile;
import com.bigwoah.flumpgdx.library.MovieMaker;

public class RenderExample {

    public static void main(String[] args) {

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height = 600;

        new LwjglApplication(new FlumpAnimations(), config);

    }

    public static class FlumpAnimations extends ApplicationAdapter {

        FlumpLibraryFile file;
        FlumpAnimation animation;

        String movieAttack = "attack";
        String movieDefeat = "defeat";
        String movieIdle = "idle";
        String movieWalk = "walk";

        SpriteBatch batch;

        @Override
        public void create () {
            batch = new SpriteBatch();
            file =  FlumpLibraryFile.deserialize("examples/assets/mascot/library.json");
            animation = MovieMaker.make(file, movieWalk);
            animation.setPosition(Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
        }

        @Override
        public void render () {
            Gdx.gl.glClearColor(1, 1, 1, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            animation.update(Gdx.graphics.getDeltaTime());
            batch.begin();
            animation.draw(batch);
            batch.end();
        }
    }


}
