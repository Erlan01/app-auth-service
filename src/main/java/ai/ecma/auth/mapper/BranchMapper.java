package ai.ecma.auth.mapper;

import ai.ecma.lib.entity.Branch;
import ai.ecma.lib.payload.resp.BranchRespDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Murtazayev Muhammad
 * @since 22.01.2022
 */
@Mapper(componentModel = "spring")
public interface BranchMapper {
    @Mapping(target = "addressId", source = "address.id")
    BranchRespDto toBranchRespDto(Branch branch);
}
