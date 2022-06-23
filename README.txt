This is Android Scanner application
which will detect quadratic shape and cut out it forming new image in 2D 
beeing oriented perpendicularly to user           

you need to have OpenCV sdk and modify the next file to keep orientation of camera properlly
put the next lines into initializeCamera method of JavaCameraView.java before mCamera.startPreview();

mCamera.setDisplayOrientation(90);
mCamera.setPreviewDisplay(getHolder());