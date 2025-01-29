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
import org.springframework.data.domain.PageRequest
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
        val creator = chatServiceUtil.findUserById(userId)

        val chatRoom = ChatRoom(
            roomId = ChatRoom.generateUniqueRoomId(),
            roomName = requestDTO.roomName,
            description = requestDTO.description,
            creator = creator,
            occupied = false
        )

        chatRoomRepository.save(chatRoom)

        val randomAdmin = assignRandomAdminToRoom(chatRoom)

        chatMessageService.sendSystemMessage(
            "채팅방이 생성되었습니다. 방 제목: ${requestDTO.roomName} 설명: ${requestDTO.description}",
            chatRoom.roomId,
            ChatMessage.MessageType.SYSTEM
        )

        randomAdmin?.let {
            chatMessageService.sendSystemMessage(
                "관리자 ${it.name}님이 채팅방에 배정되었습니다.",
                chatRoom.roomId,
                ChatMessage.MessageType.SYSTEM
            )
        }

        return convertToDTO(chatRoom, userId)
    }

    /**
     * 관리자 중 랜덤으로 한 명을 채팅방에 배정
     */
    private fun assignRandomAdminToRoom(chatRoom: ChatRoom): User? {
        val admins = userRepository.findByRole(Role.ADMIN)

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
    fun getAllChatRooms(pageRequestDTO: PageRequestDTO): PageDTO<ChatRoomResponseDTO> {
        val pageable = pageRequestDTO.toPageRequest()
        val chatRoomsPage = chatRoomRepository.findAll(pageable)

        val content = chatRoomsPage.map { convertToDTO(it, null) }.toList()

        val meta = PageMetaDTO(
            pageRequestDTO.page,
            pageRequestDTO.size,
            chatRoomsPage.totalElements
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

        if (chatRoomsPage.isEmpty) {
            throw IllegalArgumentException("생성한 채팅방이 없습니다.")
        }

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

        return ChatRoomResponseDTO(
            roomId = chatRoom.roomId,
            roomName = chatRoom.roomName,
            description = chatRoom.description,
            occupied = chatRoom.occupied,
            lastMessage = chatRoom.lastMessage!!,
            lastMessageTime = chatRoom.lastMessageTime!!,
            createdAt = chatRoom.createdAt,
            opponentId = opponent?.id,
            opponentName = opponent?.name ?: "상대방 없음",
            opponentProfileImage = opponent?.profileImg,
            opponentStatus = opponentStatus
        )
    }
}