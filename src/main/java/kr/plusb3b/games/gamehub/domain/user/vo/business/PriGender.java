package kr.plusb3b.games.gamehub.domain.user.vo.business;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class PriGender {

    private String priGender;

    public PriGender(){

    }

    private PriGender(String priGender){
        this.priGender = priGender;
    }

    public static PriGender of(String priGender){
        if(priGender == null)
            throw new IllegalArgumentException("성별이 공백입니다");

        return new PriGender(priGender);
    }

    @Override
    public String toString() {
        return "PriGender{" +
                "priGender='" + priGender + '\'' +
                '}';
    }
}
