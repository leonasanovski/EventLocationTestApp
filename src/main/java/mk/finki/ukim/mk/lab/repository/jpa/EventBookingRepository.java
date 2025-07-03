package mk.finki.ukim.mk.lab.repository.jpa;

import mk.finki.ukim.mk.lab.model.EventBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EventBookingRepository extends JpaRepository<EventBooking, Long> {

    @Query("SELECT COALESCE(SUM(b.numberOfTickets), 0) FROM EventBooking b WHERE b.event.id = :eventId")
    int countTicketsByEventId(@Param("eventId") Long eventId);
}