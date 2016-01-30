

#ifndef I_SENSOR_H
#define I_SENSOR_H

class ISensor{
  protected:
    virtual int getX();
    virtual int getY();
    virtual int getZ();
    
    virtual void read();
};

#endif /* I_SENSOR_H */
