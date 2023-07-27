package com.brother.graduationwork.domain;

import com.brother.graduationwork.domain.Status.RoomStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@NoArgsConstructor
public class Room {

    @Id
    @GeneratedValue
    @Column(name = "ROOM_ID")
    private Long id;

    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private final List<User> users = new ArrayList<>();

    private String title;

    private String gatheringPlace;

    private String gatheringDetailPlace;

    private LocalDate createdDate;

    private String createdBy;

    private int minimumOrderAmount;

    private int currentAmount;

    private int maximumPeople;

    private int currNumOfPeople;

    private int figure;

    @Enumerated(EnumType.STRING)
    private RoomStatus roomStatus;

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public void addFigure() {
        this.figure += 1;
    }

    public void subtractFigure() {
        this.figure -= 1;
    }

    public void resetFigure() {
        this.figure = 0;
    }

    public void addPerson(User user) {
        users.add(user);
        user.setRoom(this);
        currNumOfPeople += 1;
    }

    public void deletePerson(User user) {
        users.remove(user);
        currNumOfPeople -= 1;
    }

    public void addCurrAmount(int before, int after) {
        this.currentAmount = this.currentAmount - before + after;
    }

    @Builder
    public Room(String title, String gatheringPlace, String gatheringDetailPlace, String createdBy, int minimumOrderAmount, int numOfPeople) {
        this.title = title;
        this.gatheringPlace = gatheringPlace;
        this.gatheringDetailPlace = gatheringDetailPlace;
        this.createdDate = LocalDate.now();
        this.roomStatus = RoomStatus.RECRUIT;
        this.createdBy = createdBy;
        this.minimumOrderAmount = minimumOrderAmount;
        this.currentAmount = 0;
        this.maximumPeople = numOfPeople;
        this.currNumOfPeople = 0;
        this.figure = 0;
    }
}
