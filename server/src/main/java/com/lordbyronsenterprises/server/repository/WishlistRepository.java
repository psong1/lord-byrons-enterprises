package com.lordbyronsenterprises.server.repository;

import com.lordbyronsenterprises.server.support.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long>{
}
