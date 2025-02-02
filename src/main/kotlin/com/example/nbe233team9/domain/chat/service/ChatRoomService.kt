package com.example.nbe233team9.domain.chat.service
import com.example.nbe233team9.common.dto.PageDTO
import com.example.nbe233team9.common.dto.PageMetaDTO
import com.example.nbe233team9.common.dto.PageRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatRoomCreateRequestDTO
import com.example.nbe233team9.domain.chat.dto.ChatRoomResponseDTO
import com.example.nbe233team9.domain.chat.entity.ChatMessage
import com.example.nbe233team9.domain.chat.entity.ChatParticipant
import com.example.nbe233team9.domain.chat.entity.ChatRoom
import com.example.nbe233team9.domain.chat.repository.ChatMessageRepository
import com.example.nbe233team9.domain.chat.repository.ChatParticipantRepository
import com.example.nbe233team9.domain.chat.repository.ChatRoomRepository
import com.example.nbe233team9.domain.user.model.Role
import com.example.nbe233team9.domain.user.model.User
import com.example.nbe233team9.domain.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatRoomService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatParticipantRepository: ChatParticipantRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val userRepository: UserRepository,
    private val chatMessageService: ChatMessageService,
    private val chatServiceUtil: ChatServiceUtil,
) {

    private val random = Random()

    /**
     * 채팅방 생성
     */
    fun createChatRoom(userId: Long, requestDTO: ChatRoomCreateRequestDTO): ChatRoomResponseDTO {
        // 1️⃣ 채팅방 생성자 조회
        val creator: User = chatServiceUtil.findUserById(userId)

        // 2️⃣ 채팅방 생성
        val chatRoom = ChatRoom(
            roomId = ChatRoom.generateUniqueRoomId(),
            roomName = requestDTO.roomName,
            description = requestDTO.description,
            creator = creator,
            occupied = false
        )

        chatRoomRepository.save(chatRoom)


        val creatorParticipant = ChatParticipant(
            chatRoom = chatRoom,
            user = creator,
            isAdmin = false,  // 생성자는 관리자가 아님
            isActive = true   // 기본적으로 활성화 상태
        )
        chatParticipantRepository.save(creatorParticipant)

        // 3️⃣ 랜덤 관리자 배정
        val randomAdmin: User? = assignRandomAdminToRoom(chatRoom)

        // 4️⃣ 시스템 메시지 전송 (관리자 배정 알림)
        randomAdmin?.let {
            chatMessageService.sendSystemMessage(
                "관리자 ${it.name}님이 채팅방에 배정되었습니다.",
                chatRoom.roomId,
                ChatMessage.MessageType.SYSTEM
            )
        }

        // 6️⃣ DTO 변환 후 반환
        return convertToDTO(chatRoom, userId)
    }

    /**
     * 관리자 중 랜덤으로 한 명을 채팅방에 배정
     */
    private fun assignRandomAdminToRoom(chatRoom: ChatRoom): User? {
        val admins: List<User> = userRepository.findByRole(Role.ADMIN)

        if (admins.isEmpty()) return null

        val selectedAdmin = admins[random.nextInt(admins.size)]

        val adminParticipant = ChatParticipant(
            chatRoom = chatRoom,
            user = selectedAdmin,
            isAdmin = true,
            isActive = true
        )

        chatParticipantRepository.save(adminParticipant)

        chatRoom.occupied = true
        chatRoomRepository.save(chatRoom)

        return selectedAdmin
    }

    /**
     * 전체 채팅방 조회
     */
    fun getAllChatRooms(adminId: Long, pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable: Pageable = pageRequestDTO.toPageRequest()

        // 관리자 User 객체 조회 (필요한 경우)
        val admin: User = userRepository.findById(adminId)
            .orElseThrow { IllegalArgumentException("관리자 정보를 찾을 수 없습니다.") }

        // 관리자가 포함된 채팅방 조회
        val adminChatRooms: Page<ChatRoom> = chatRoomRepository.findAdminChatRooms(admin, pageable)

        // DTO 변환
        val content: List<ChatRoomResponseDTO> = adminChatRooms.content.map { chatRoom ->
            convertToDTO(chatRoom, admin.id)
        }

        // 페이징 메타데이터 생성
        val meta = PageMetaDTO(
            pageRequestDTO.page,
            pageRequestDTO.size,
            adminChatRooms.totalElements
        )

        return PageDTO(content, meta)
    }


    /**
     * 대기 중인(관리자가 없는) 채팅방 조회
     */
    fun getWaitingRooms(pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable = pageRequestDTO.toPageRequest()
        val waitingRoomsPage = chatRoomRepository.findByOccupiedFalse(pageable)

        val content = waitingRoomsPage.map { convertToDTO(it, null) }.toList()

        val meta = PageMetaDTO(
            pageRequestDTO.page,
            pageRequestDTO.size,
            waitingRoomsPage.totalElements
        )

        return PageDTO(content, meta)
    }

    /**
     * ✅ 관리자 전용 - 전체 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    fun searchAllChatRooms(keyword: String, pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable = pageRequestDTO.toPageRequest()
        val finalResults = mutableListOf<ChatRoomResponseDTO>()
        var currentPage = pageable.pageNumber
        var hasMoreData = true

        while (finalResults.size < pageable.pageSize && hasMoreData) {
            val roomsByNameOrDescriptionPage = chatRoomRepository.findByRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                keyword, keyword, PageRequest.of(currentPage, pageable.pageSize)
            )

            val roomIdsByMessages = chatMessageRepository.findDistinctChatRoomIdsByKeyword(keyword)

            val roomsByMessagesPage = chatRoomRepository.findByRoomIdIn(
                roomIdsByMessages, PageRequest.of(currentPage, pageable.pageSize)
            )

            val distinctRooms = (roomsByNameOrDescriptionPage.content + roomsByMessagesPage.content).distinct()
            val pageResults = distinctRooms.map { convertToDTO(it, null) }

            finalResults.addAll(pageResults)

            hasMoreData = roomsByNameOrDescriptionPage.hasNext() || roomsByMessagesPage.hasNext()
            currentPage++
        }

        val meta = PageMetaDTO(pageRequestDTO.page, pageRequestDTO.size, finalResults.size.toLong())
        return PageDTO(finalResults, meta)
    }

    /**
     * ✅ 사용자 전용 - 본인이 참여하거나 생성한 채팅방 검색
     * - 채팅방 이름, 설명, 메시지 내용에서 키워드를 검색
     */
    fun searchUserChatRooms(userId: Long, keyword: String, pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable = pageRequestDTO.toPageRequest()
        val finalResults = mutableListOf<ChatRoomResponseDTO>()
        var currentPage = pageable.pageNumber
        var hasMoreData = true

        while (finalResults.size < pageable.pageSize && hasMoreData) {
            val createdRoomsPage = chatRoomRepository.findByCreatorIdAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                userId, keyword, keyword, PageRequest.of(currentPage, pageable.pageSize)
            )

            val participantRoomIds = chatParticipantRepository.findRoomIdsByUserId(userId)

            val participantRoomsPage = chatRoomRepository.findByRoomIdInAndRoomNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                participantRoomIds, keyword, keyword, PageRequest.of(currentPage, pageable.pageSize)
            )

            val distinctRooms = (createdRoomsPage.content + participantRoomsPage.content).distinct()
            val pageResults = distinctRooms.map { convertToDTO(it, userId) }

            finalResults.addAll(pageResults)

            hasMoreData = createdRoomsPage.hasNext() || participantRoomsPage.hasNext()
            currentPage++
        }

        val meta = PageMetaDTO(pageRequestDTO.page, pageRequestDTO.size, finalResults.size.toLong())
        return PageDTO(finalResults, meta)
    }

    /**
     * 사용자 전용 - 본인이 생성한 채팅방 조회
     */
    fun getRoomsByUserId(userId: Long, pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable = pageRequestDTO.toPageRequest()
        val chatRoomsPage = chatRoomRepository.findByCreatorId(userId, pageable)

        val content = chatRoomsPage.map { convertToDTO(it, userId) }.toList()
        val meta = PageMetaDTO(pageRequestDTO.page, pageRequestDTO.size, chatRoomsPage.totalElements)
        return PageDTO(content, meta)
    }

    /**
     * ChatRoom -> ChatRoomResponseDTO 변환 메서드
     */
    private fun convertToDTO(chatRoom: ChatRoom, currentUserId: Long?): ChatRoomResponseDTO {
        val opponentParticipant = chatParticipantRepository.findByChatRoom(chatRoom)
            .firstOrNull { it.user.id != currentUserId }

        val opponent = opponentParticipant?.user

        val opponentStatus = opponent?.let {
            chatServiceUtil.getUserStatus(it.id.toString())
        } ?: "disconnected"

        val lastMessage = chatRoom.lastMessage ?: "메시지가 없습니다."
        val lastMessageTime = chatRoom.lastMessageTime ?: chatRoom.createdAt

        return ChatRoomResponseDTO(
            roomId = chatRoom.roomId,
            roomName = chatRoom.roomName,
            description = chatRoom.description,
            occupied = chatRoom.occupied,
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            createdAt = chatRoom.createdAt,
            opponentId = opponent?.id,
            opponentName = opponent?.name ?: "상대방 없음",
            opponentProfileImage = opponent?.profileImg,
            opponentStatus = opponentStatus
        )
    }
}