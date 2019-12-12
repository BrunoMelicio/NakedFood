from os import listdir
from numpy import asarray
from keras.preprocessing.image import load_img
from keras.preprocessing.image import img_to_array
from sklearn.model_selection import train_test_split
from pathlib import PureWindowsPath, Path

l = 3
test_size = 0.33


def load_dataset():
    # define the location of the dataset
    # if you are using Windows please use PureWindowsFile
    folder = Path('')
    photos, labels = list(), list()
    for file in listdir(Path(folder)):
        label = file[0:l]
        photo = load_img(Path(folder.joinpath(file)), target_size=(224, 224))
        photo = img_to_array(photo)
        photos.append(photo)
        labels.append(label)
    photos = asarray(photos)
    labels = asarray(labels)
    X_train, X_test, y_train, y_test = train_test_split(photos, labels, test_size=test_size, random_state=30)

    return X_train, X_test, y_train, y_test


X_train, X_test, y_train, y_test = load_dataset()
