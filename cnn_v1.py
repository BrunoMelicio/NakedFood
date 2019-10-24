import numpy as np
import keras

from keras import layers
from keras.layers import Input, Dense, Activation, ZeroPadding2D, BatchNormalization, Flatten, Conv2D, Add, concatenate
from keras.layers import MaxPooling2D, Dropout
from keras.models import Model
from keras.models import Sequential
from keras.preprocessing import image
from keras.applications.imagenet_utils import preprocess_input

import keras.backend as K
K.set_image_data_format('channels_last')
import matplotlib.pyplot as plt
from matplotlib.pyplot import imshow



class FoodModel:
  def __init__(self,input_shape,optimizer,loss='categorical_crossentropy',metrics=['accuracy'],epochs=200,batch_size=32):
    self.model = None
    self.history = None
    self.input_shape = input_shape
    self.optimizer = optimizer
    self.loss = loss
    self.metrics = metrics
    self.epochs = epochs
    self.batch_size = batch_size

  def createModel(self, pretrained_weights=None):
    #Mixed CNN
    
    #Input
    X_input = Input(self.input_shape)
    
    X_shortcut = X_input # Store the initial value of X in a variable
    #Make the shortcut go through a CONV -> BATCH_NORM -> RELU
    X_shortcut = Conv2D(filters = 64,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv0')(X_shortcut)
    X_shortcut = BatchNormalization(axis=3, name='BatchNorm0')(X_shortcut)
    X_shortcut = Activation('relu')(X_shortcut)
    
    #RESIDUAL BLOCK = INCEPTION_BLOCK -> CONV -> BATCH_NORM -> RELU
    #Inception Block
    X1 = Conv2D(filters = 64,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv1')(X_input)
    X2 = Conv2D(filters = 96,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv2')(X_input)
    X2 = Conv2D(filters = 128,kernel_size = 3,strides = 1,padding = 'same',activation='relu',name='Conv3')(X2)
    X3 = Conv2D(filters = 96,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv4')(X_input)
    X3 = Conv2D(filters = 32,kernel_size = 5,strides = 1,padding = 'same',activation='relu',name='Conv5')(X3)
    X4 = MaxPooling2D(pool_size= 3,strides = 1,padding = 'same',name='Pool1')(X_input)
    X4 = Conv2D(filters = 32,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv6')(X4)
    inception_block = concatenate([X1,X2,X3,X4],axis=3)

    #Conv
    X = Conv2D(filters = 64,kernel_size = 3,strides = 1,padding = 'same',name='Conv7')(inception_block)
    X = BatchNormalization(axis=3, name='BatchNorm1')(X)
    X = Activation('relu')(X)
    
    #Add Skip Connection
    X = Add()([X, X_shortcut])
    X = Activation('relu')(X)
    X = MaxPooling2D(pool_size = 2, strides = 2,name='Pool3')(X)
    
    X = Conv2D(filters = 128,kernel_size = 1,strides = 1,padding = 'valid',activation='relu',name='Conv8')(X)
    X = Conv2D(filters = 32,kernel_size = 3,strides = 1,padding = 'same',activation='relu',name='Conv9')(X)
    X = BatchNormalization(axis=3, name='BatchNorm2')(X)
    X = Activation('relu')(X)
    X = MaxPooling2D(pool_size = 2, strides = 2,name='Pool4')(X)
    
    #Conv
    X = Conv2D(filters = 256,kernel_size = 1,strides = 1,padding = 'same',name='Conv10')(X)
    X = Conv2D(filters = 64,kernel_size = 3,strides = 1,padding = 'same',activation='relu',name='Conv11')(X)
    X = BatchNormalization(axis=3, name='BatchNorm3')(X)
    X = Activation('relu')(X)
    X = MaxPooling2D(pool_size = 2, strides = 2,name='Pool5')(X)
    
    #Flatten
    X = Flatten()(X)
    
    #Dense
    X = Dense(units=256,activation='relu',name='FC15')(X)
    X = Dropout(0.5)(X)
    
    X = Dense(units=128,activation='relu',name='FC16')(X)
    X = Dropout(0.3)(X)
    
    X = Dense(units=64,activation='relu',name='FC17')(X)
    
    X = Dense(units=22,activation='softmax',name='Out18')(X)
    
    self.model = Model(inputs=X_input, outputs=X, name='FoodModel')
    
    if(pretrained_weights):
          self.model.load_weights(pretrained_weights)

  def print_model(self):
        self.model.summary()
        
  def compileM(self):
      self.model.compile(optimizer=self.optimizer, loss=self.loss, metrics=self.metrics)

  def train(self, X, y, val_split=0.1, shuffle=True, callbacks=[]):
      self.history = self.model.fit(X,y,epochs=self.epochs,batch_size=self.batch_size,validation_split=val_split,shuffle=shuffle, callbacks=callbacks)

  def test(self, X, y):
      return self.model.evaluate(X, y, batch_size=self.batch_size)

  def plot_cost(self):
      plt.figure()
      plt.xlabel('Epoch')
      plt.ylabel('Loss')
      plt.plot(self.history.epoch, np.array(self.history.history['loss']), label='Train Loss')
      plt.plot(self.history.epoch, np.array(self.history.history['val_loss']), label='Val Loss')
      plt.legend()
      plt.ylim([0,1])

  def predict(self,X):
      return self.model.predict(X)

  def saveModel(self,folder):
      self.model.save(folder)

  def saveWeights(self,folder):
      self.model.save_weights(folder)


#How to use it

'''

optimizer = keras.optimizers.Adam(lr=0.001, beta_1=0.9, beta_2=0.999, epsilon=None, decay=0.0, amsgrad=False)

model = FoodModel(input_shape=(64,64,3),optimizer=optimizer, epochs=100,batch_size=32)

model.createModel()

#model.print_model()

model.compileM()

model.train(X,y)

import h5py

model.saveModel("Saved/train2Mod1Relu100ep.h5")
model.saveWeights("Saved/train2Mod1Relu100epWeights.h5")

'''
