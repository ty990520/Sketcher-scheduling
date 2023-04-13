package sketcher.scheduling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnCountAndObject<T> {
    private int count;
    private T data;
    //object 타입으로 반환하기 위함 (배열타입으로 반환하면 유연성 떨어짐)
}