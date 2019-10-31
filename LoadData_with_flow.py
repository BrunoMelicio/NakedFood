from os import makedirs
from os import listdir
from shutil import copyfile
from random import seed
from random import random
from CreateLabelsDictonary import create_labels_dictionary
from pathlib import PureWindowsPath, Path
from keras.preprocessing.image import ImageDataGenerator

l = 3
# set the location of the dataset
dataset_home = Path('')
labels_dictionary = create_labels_dictionary('labels.txt')
sub_directories = ['train/, test/']
# Make Directories
for subdir in sub_directories:
    labels_dirctories = {str(x).join('/') for _, x in labels_dictionary.items()}
    for labeldir in labels_dictionary:
        new_directory = Path(dataset_home + subdir + labeldir)
        makedirs(new_directory, exist_ok=True)

# set random seed
seed(1)
# Define the data used fro validation ratio
validation_ratio = 0.25

# Copy images into subdirectories
# set the location of the source directory where dataset is located
source_directory = dataset_home
for file in listdir(source_directory):
    source = Path(source_directory + '/' + file)
    destination_directory = '/train'
    if random() < validation_ratio:
        destination_directory = '/test'
    destination = Path(dataset_home + destination_directory + labels_dictionary[file[0:l]] + '/' + file)
    copyfile(source, destination)

# prepare iterations
dataGenerator = ImageDataGenerator()

train = dataGenerator.flow_from_directory(Path(dataset_home + 'train/'), class_mode='categorical', batch_size=32,
                                          target_size=(200, 200))
test = dataGenerator.flow_from_directory(Path(dataset_home + 'test/'), class_mode='categorical', batch_size=32,
                                         target_size=(200, 200))
