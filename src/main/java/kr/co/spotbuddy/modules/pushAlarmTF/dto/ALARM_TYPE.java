package kr.co.spotbuddy.modules.pushAlarmTF.dto;

import kr.co.spotbuddy.infra.domain.PushAlarmTF;

public enum ALARM_TYPE {
    MESSAGE {
        @Override
        public void changeAlarmStatus(PushAlarmTF pushAlarmTF, PushAlarmTypeStatus pushAlarmTypeStatus) {
            pushAlarmTF.setMessageTurnOn(pushAlarmTypeStatus.isPushAlarmOn());
        }
    },
    ACTIVITY {
        @Override
        public void changeAlarmStatus(PushAlarmTF pushAlarmTF, PushAlarmTypeStatus pushAlarmTypeStatus) {
            pushAlarmTF.setActivityTurnOn(pushAlarmTypeStatus.isPushAlarmOn());
        }
    },
    SCHEDULE {
        @Override
        public void changeAlarmStatus(PushAlarmTF pushAlarmTF, PushAlarmTypeStatus pushAlarmTypeStatus) {
            pushAlarmTF.setScheduleTurnOn(pushAlarmTypeStatus.isPushAlarmOn());
        }
    },
    PROMOTION {
        @Override
        public void changeAlarmStatus(PushAlarmTF pushAlarmTF, PushAlarmTypeStatus pushAlarmTypeStatus) {
            pushAlarmTF.setPromoTurnOn(pushAlarmTypeStatus.isPushAlarmOn());
        }
    };

    public abstract void changeAlarmStatus(PushAlarmTF pushAlarmTF, PushAlarmTypeStatus pushAlarmTypeStatus);
}
