package com.vedicpooja.catalog;

import com.vedicpooja.purohit.Purohit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PurohitServiceOfferingRepository extends JpaRepository&lt;PurohitServiceOffering, Long&gt; {
    List&lt;PurohitServiceOffering&gt; findByPurohit(Purohit purohit);
    Optional&lt;PurohitServiceOffering&gt; findByPurohitIdAndServiceId(Long purohitId, Long serviceId);
}