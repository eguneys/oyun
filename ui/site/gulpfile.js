const gulp = require('gulp');
const source = require('vinyl-source-stream');
const buffer = require('vinyl-buffer');
const watchify = require('watchify');
const browserify = require('browserify');
const terser = require('gulp-terser');
const concat = require('gulp-concat');


const browserifyOpts = (entries, debug) => ({
  entries,
  standalone: 'Oyunkeyf',
  debug
});
const destinationPath = '../../public/compiled/';
const destination = () => gulp.dest(destinationPath);
const fileBaseName = 'oyunkeyf.site';

const jqueryFill = () => gulp.src('src/jquery.fill.js')
      .pipe(buffer())
      .pipe(terser())
      .pipe(gulp.dest('./dist'));

const devSource = () => browserify(browserifyOpts('src/index.js', true))
      .transform('babelify',
                 { presets: ['@babel/preset-env'] })
      .bundle()
      .pipe(source(`${fileBaseName}.js`))
      .pipe(destination());


function makeDependencies(filename) {
  return function bundleDeps() {
    return gulp.src([
      '../../public/javascripts/vendor/jquery.min.js',
      './dist/jquery.fill.js'
    ]).pipe(concat(filename))
      .pipe(destination());
  };
}

const deps = makeDependencies('oyunkeyf.deps.js');

const tasks = [jqueryFill, deps];

const dev = gulp.series(tasks.concat([devSource]));

gulp.task('dev', gulp.series(tasks, dev));
gulp.task('default', gulp.series(tasks, dev, () => gulp.watch('src/**/*.js', dev)));
