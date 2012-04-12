/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nebula.sheeptester.model;

import com.nebula.sheeptester.exec.ChannelProcess;

/**
 *
 * @author shevek
 */
public enum SheepOperator {

    CREATE_VDI() {

        @Override
        public ChannelProcess newProcess(Sheep sheep, Vdi vdi) {
            return sheep.createVdi(vdi);
        }
    },
    DELETE_VDI() {

        @Override
        public ChannelProcess newProcess(Sheep sheep, Vdi vdi) {
            return sheep.deleteVdi(vdi);
        }
    },
    READ_VDI() {

        @Override
        public ChannelProcess newProcess(Sheep sheep, Vdi vdi) {
            return sheep.readVdi(vdi);
        }
    },
    WRITE_VDI() {

        @Override
        public ChannelProcess newProcess(Sheep sheep, Vdi vdi) {
            return sheep.writeVdi(vdi);
        }
    };

    public abstract ChannelProcess newProcess(Sheep sheep, Vdi vdi);
}
